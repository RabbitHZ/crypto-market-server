package com.cyptomarket.server.service;

import com.cyptomarket.server.dto.OrderHistoryV1;
import com.cyptomarket.server.dto.OrderRequestV1;
import com.cyptomarket.server.dto.OrderResponseV1;
import com.cyptomarket.server.entity.Order;
import com.cyptomarket.server.entity.Symbol;
import com.cyptomarket.server.entity.User;
import com.cyptomarket.server.entity.enums.OrderState;
import com.cyptomarket.server.entity.enums.OrderStatus;
import com.cyptomarket.server.entity.enums.OrderType;
import com.cyptomarket.server.repository.OrderRepository;
import com.cyptomarket.server.repository.SymbolRepository;
import com.cyptomarket.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {
    @Mock
    private SymbolRepository symbolRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderService orderService;
    private OrderRequestV1 validRequest;
    private Symbol symbol;
    private User user;
    private Order order;
    private LocalDate fixedNow;
    private OrderRequestV1 request;

    @BeforeEach
    void setUp(){
        validRequest = new OrderRequestV1(
                "AAPL_KRW",
                OrderType.LIMIT,
                OrderState.BUY,
                1000.0,
                10.0
        );
        user = new User(1L, 10_000_000_000.0);
        symbol = new Symbol("AAPL_KRW");
        order = Order.builder()
                .id(1L)
                .user(user)
                .symbol(symbol)
                .orderType(OrderType.LIMIT)
                .orderState(OrderState.BUY)
                .status(OrderStatus.PENDING)
                .price(1000.0)
                .quantity(10.0)
                .build();

        fixedNow = LocalDate.of(2025, 8, 24);

        request = new OrderRequestV1(
                "BTC/USDT",
                OrderType.LIMIT,
                OrderState.BUY,
                60000.0,
                2.0
        );
    }

    @Test
    @DisplayName("주문 생성 성공 - 유효한 요청")
    void createOrder_success() {
        // Given
        Long userId = 1L;
        when(symbolRepository.findBySymbol("AAPL_KRW")).thenReturn(Optional.of(symbol));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        OrderResponseV1 response = orderService.createOrder(validRequest, userId);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.orderId());
        assertEquals(OrderState.BUY, response.orderState());
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(userId, response.userId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 실패 - 유효하지 않은 종목 코드")
    void createOrder_invalidSymbol_throwsException() {
        // Given
        Long userId = 1L;
        when(symbolRepository.findBySymbol("INVALID_KRW")).thenReturn(Optional.empty());
        OrderRequestV1 invalidRequest = new OrderRequestV1(
                "INVALID_KRW",
                OrderType.LIMIT,
                OrderState.BUY,
                1000.0,
                10.0
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(invalidRequest, userId));
        assertEquals("유효하지 않은 종목 코드입니다.", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 실패 - 사용자 찾을 수 없음")
    void createOrder_userNotFound_throwsException() {
        // Given
        Long userId = 1L;
        when(symbolRepository.findBySymbol("AAPL_KRW")).thenReturn(Optional.of(symbol));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(validRequest, userId));
        assertEquals("유효하지 않은 사용자 ID입니다.", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 실패 - 잔고 부족")
    void createOrder_insufficientBalance_throwsException() {
        // Given
        Long userId = 1L;
        User poorUser = new User(userId, 500.0);

        when(symbolRepository.findBySymbol("AAPL_KRW")).thenReturn(Optional.of(symbol));
        when(userRepository.findById(userId)).thenReturn(Optional.of(poorUser));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(validRequest, userId));
        assertEquals("잔고가 부족합니다.", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 실패 - KRW 최소 금액 미달")
    void createOrder_invalidAmountKRW_belowMinimum_throwsException() {
        // Given
        Long userId = 1L;
        OrderRequestV1 invalidRequest = new OrderRequestV1(
                "AAPL_KRW",
                OrderType.LIMIT,
                OrderState.BUY,
                100.0,
                10.0 // 총액 1,000 < 5,000
        );
        when(symbolRepository.findBySymbol("AAPL_KRW")).thenReturn(Optional.of(symbol));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(invalidRequest, userId));
        assertEquals("KRW 주문 금액은 5,000~1,000,000,000 사이여야 합니다.", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 실패 - KRW 최대 금액 초과")
    void createOrder_invalidAmountKRW_aboveMaximum_throwsException() {
        // Given
        Long userId = 1L;
        OrderRequestV1 invalidRequest = new OrderRequestV1(
                "AAPL_KRW",
                OrderType.LIMIT,
                OrderState.BUY,
                100_000_000.0,
                100.0 // 총액 10,000,000,000 > 1,000,000,000
        );
        when(symbolRepository.findBySymbol("AAPL_KRW")).thenReturn(Optional.of(symbol));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(invalidRequest, userId));
        assertEquals("KRW 주문 금액은 5,000~1,000,000,000 사이여야 합니다.", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetOrderHistory_Success() {
        // Given
        Long userId = 1L;
        LocalDate startDate = fixedNow.minusDays(1);
        LocalDate endDate = fixedNow;
        OrderState orderState = OrderState.BUY;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        Symbol symbol = new Symbol("BTC/KRW");
        User user = new User(userId, 1000.0);
        Order order = Order.builder()
                .id(1L)
                .user(user)
                .symbol(symbol)
                .orderType(OrderType.LIMIT)
                .orderState(OrderState.BUY)
                .price(50000.0)
                .quantity(1.0)
                .executedQuantity(0.5)
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findByUserIdAndCreatedAtBetweenAndOrderState(
                anyLong(), any(LocalDate.class), any(LocalDate.class), any(OrderState.class), any(Pageable.class)))
                .thenReturn(List.of(order));

        // When
        List<OrderHistoryV1> result = orderService.getOrderHistory(userId, startDate, endDate, orderState, pageable);

        // Then
        assertEquals(1, result.size());
        OrderHistoryV1 orderHistory = result.get(0);
        assertEquals(1L, orderHistory.orderId());
        assertEquals("BTC/KRW", orderHistory.symbol());
        assertEquals(OrderType.LIMIT, orderHistory.orderType());
        assertEquals(OrderState.BUY, orderHistory.orderState());
        assertEquals(50000.0, orderHistory.price());
        assertEquals(1.0, orderHistory.quantity());
        assertEquals(0.5, orderHistory.executedQuantity());
        assertEquals(OrderStatus.PENDING, orderHistory.status());
        assertEquals(fixedNow.toString(), orderHistory.createdAt());
        assertEquals(userId, orderHistory.userId());
    }

    @Test
    void testGetOrderHistory_EmptyResult() {
        // Given
        Long userId = 1L;
        LocalDate startDate = fixedNow.minusDays(1);
        LocalDate endDate = fixedNow;
        OrderState orderState = OrderState.BUY;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        when(orderRepository.findByUserIdAndCreatedAtBetweenAndOrderState(
                anyLong(), any(LocalDate.class), any(LocalDate.class), any(OrderState.class), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        // When
        List<OrderHistoryV1> result = orderService.getOrderHistory(userId, startDate, endDate, orderState, pageable);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void updateOrder_success() {
        // Given
        Long userId = 1L;
        Long orderId = 1L;
        Symbol symbol = new Symbol("BTC/USDT");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(symbolRepository.findBySymbol("BTC/USDT")).thenReturn(Optional.of(symbol));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        OrderResponseV1 response = orderService.updateOrder(orderId, userId, request);

        // Then
        assertNotNull(response);
        assertEquals(orderId, response.orderId());
        assertEquals(request.orderState(), response.orderState());
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(0.0, response.executedQuantity());
        assertEquals(userId, response.userId());
        verify(orderRepository).save(any(Order.class));
    }
}
