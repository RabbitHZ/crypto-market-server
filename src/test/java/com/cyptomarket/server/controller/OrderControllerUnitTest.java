package com.cyptomarket.server.controller;


import com.cyptomarket.server.dto.OrderHistoryV1;
import com.cyptomarket.server.dto.OrderQueryRequestV1;
import com.cyptomarket.server.dto.OrderRequestV1;
import com.cyptomarket.server.dto.OrderResponseV1;
import com.cyptomarket.server.entity.enums.OrderState;
import com.cyptomarket.server.entity.enums.OrderStatus;
import com.cyptomarket.server.entity.enums.OrderType;
import com.cyptomarket.server.service.OrderService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@ExtendWith(MockitoExtension.class)
public class OrderControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private OrderController orderController;
    @MockBean
    private OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;

    private OrderRequestV1 validRequest;
    private OrderQueryRequestV1 request;
    private OrderHistoryV1 orderHistory;

    @BeforeEach
    void setUp() {
        validRequest = new OrderRequestV1(
                "AAPL_KRW",
                OrderType.LIMIT,
                OrderState.BUY,
                100.0,
                10.0
        );
        request = new OrderQueryRequestV1(
                1L,
                OrderState.BUY,
                LocalDate.now().minusDays(1),
                LocalDate.now(),
                0,
                10
        );
    }

    @Test
    void createOrder_success() throws Exception {
        Long userId = 1L;
        OrderResponseV1 response = new OrderResponseV1(1L, OrderState.BUY, OrderStatus.PENDING, 0, userId);
        when(orderService.createOrder(any(OrderRequestV1.class), eq(userId))).thenReturn(response);

        mockMvc.perform(post("/v1/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))) // content로 수정
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.orderState").value("BUY"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void createOrder_invalidRequest_return400() throws Exception {
        // Given
        OrderRequestV1 invalidReqeust = new OrderRequestV1(
                "",
                null,
                OrderState.BUY,
                -100.0,
                0.0
        );
        //When & Then
        mockMvc.perform(post("/v1/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidReqeust)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("유효성 검사 실패"));
    }

    @Test
    void createOrder_unauthorized_returns401() throws Exception {
        // Given
        try (MockedStatic<OrderController> mockedStatic = Mockito.mockStatic(OrderController.class)) {
            mockedStatic.when(OrderController::getAuthenticatedUserId).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/v1/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("인증 실패"))
                .andExpect(jsonPath("$.message").value("인증되지 않은 사용자입니다."));
        }
    }

    @Test
    void getOrderHistory_Success() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        List<OrderHistoryV1> orders = List.of(
                new OrderHistoryV1(1L, "BTC/KRW", OrderType.LIMIT, OrderState.BUY,
                        50000.0, 1.0, 0.5, OrderStatus.PENDING,
                        LocalDateTime.now().toString(), 1L)
        );
        when(orderService.getOrderHistory(anyLong(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/v1/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1L))
                .andExpect(jsonPath("$[0].symbol").value("BTC/KRW"))
                .andExpect(jsonPath("$[0].orderState").value("BUY"));
    }

    @Test
    void getOrderHistory_InvalidDateRange() throws Exception {
        // Given
        Long userId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(1); // 시작 날짜가 종료 날짜보다 늦음
        OrderQueryRequestV1 request = new OrderQueryRequestV1(
                userId, OrderState.BUY,
                startDate, endDate, 0, 10);

        // Then
        assertThrows(IllegalArgumentException.class, () -> orderController.getOrderHistory(request),
                "시작일은 종료일보다 늦을 수 없습니다.");

        // MockMvc 테스트
        mockMvc.perform(get("/v1/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderHistory_OverFiveYearRange() throws Exception {
        // Given
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusYears(6);
        LocalDate endDate = LocalDate.now();
        OrderQueryRequestV1 request = new OrderQueryRequestV1(
                userId, OrderState.BUY,
                startDate, endDate, 0, 10);

        // Then
        assertThrows(IllegalArgumentException.class, () -> orderController.getOrderHistory(request),
                "조회 기간은 5년을 초과할 수 없습니다.");

        // MockMvc 테스트
        mockMvc.perform(get("/v1/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderHistory_EndDateAfterNow() throws Exception {
        // Given
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1); // 종료일이 현재 날짜를 초과
        OrderQueryRequestV1 request = new OrderQueryRequestV1(
                userId, OrderState.BUY,
                startDate, endDate, 0, 10);

        // Then
        assertThrows(IllegalArgumentException.class, () -> orderController.getOrderHistory(request),
                "종료일은 현재 날짜를 초과할 수 없습니다.");

        // MockMvc 테스트
        mockMvc.perform(get("/v1/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrder_success() throws Exception {
        // Given
        Long orderId = 1L;
        Long userId = 1L;
        OrderRequestV1 request = new OrderRequestV1("BTC", OrderType.LIMIT, OrderState.BUY, 50000.0, 1.5);
        OrderResponseV1 response = new OrderResponseV1(orderId, OrderState.BUY, OrderStatus.PENDING, 0.0, userId);

        when(orderService.updateOrder(eq(orderId), eq(userId), any(OrderRequestV1.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/v1/api/orders/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.orderState").value("BUY"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.executedQuantity").value(0.0))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void updateOrder_unauthorizedUser_throwsSecurityException() throws Exception {
        // Given
        Long orderId = 1L;

        // When & Then
        mockMvc.perform(put("/v1/api/orders/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateOrder_invalidRequestData_returnsBadRequest() throws Exception {
        // Given
        Long orderId = 1L;

        // When & Then
        mockMvc.perform(put("/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}