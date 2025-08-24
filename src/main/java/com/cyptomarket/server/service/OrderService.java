package com.cyptomarket.server.service;

import com.cyptomarket.server.dto.OrderHistoryV1;
import com.cyptomarket.server.dto.OrderRequestV1;
import com.cyptomarket.server.dto.OrderResponseV1;
import com.cyptomarket.server.entity.Order;
import com.cyptomarket.server.entity.Symbol;
import com.cyptomarket.server.entity.User;
import com.cyptomarket.server.entity.enums.OrderState;
import com.cyptomarket.server.entity.enums.OrderStatus;
import com.cyptomarket.server.repository.OrderRepository;
import com.cyptomarket.server.repository.SymbolRepository;
import com.cyptomarket.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SymbolRepository symbolRepository;

    @Transactional
    public OrderResponseV1 createOrder(OrderRequestV1 request, Long userId) {

        // 종목 유효성 검사
        Symbol symbol = symbolRepository.findBySymbol(request.symbol())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 종목 코드입니다."));

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        // 잔고 확인
        double totalAmount = request.price() * request.quantity();
        if (!hasSufficientBalance(userId, totalAmount)) {
            throw new IllegalArgumentException("잔고가 부족합니다.");
        }

        // 최소/최대 금액 검증
        validateAmount(request);

        // 주문 생성
        Order order = Order.createOrder(
                user,
                symbol,
                request.orderType(),
                request.orderState(),
                request.price(),
                request.quantity()
        );

        // Order 저장
        Order savedOrder = orderRepository.save(order);

        return new OrderResponseV1(
                savedOrder.getId(),
                savedOrder.getOrderState(),
                savedOrder.getStatus(),
                savedOrder.getExecutedQuantity(),
                userId
        );
    }

    private boolean hasSufficientBalance(Long userId, double requiredAmount) {
        // DB에서 사용자 잔고 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return user.getBalance() >= requiredAmount;
    }

    public List<OrderHistoryV1> getOrderHistory(Long userId, LocalDate startDate, LocalDate endDate,
                                                OrderState orderState, Pageable pageable) {

        // startDate를 LocalDateTime의 시작 시간(00:00:00)으로 변환
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        // endDate를 LocalDateTime의 종료 시간(23:59:59)으로 변환
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        List<Order> orders = orderRepository.findByUserIdAndCreatedAtBetweenAndOrderState(
                userId,
                startDate,
                endDate,
                orderState,
                pageable
        );

        return orders.stream()
                .map(order -> new OrderHistoryV1(
                        order.getId(),
                        order.getSymbol().getSymbol(),
                        order.getOrderType(),
                        order.getOrderState(),
                        order.getPrice(),
                        order.getQuantity(),
                        order.getExecutedQuantity(),
                        order.getStatus(),
                        order.getCreatedAt().toString(),
                        order.getUser().getId()
                ))
                .collect(Collectors.toList());
    }

    public OrderResponseV1 updateOrder(Long orderId, Long userId, OrderRequestV1 request) {
        // 주문 ID로 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 ID를 찾을 수 없습니다: " + orderId));

        Symbol symbol = symbolRepository.findBySymbol(request.symbol())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 종목 코드입니다."));

        // 주문 소유자 확인
        if (!order.getUser().getId().equals(userId)) {
            throw new SecurityException("해당 주문에 대한 수정 권한이 없습니다.");
        }

        // 주문 상태 확인 (체결 대기 중인 주문만 수정 가능)
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("체결 대기 중인 주문만 수정할 수 있습니다.");
        }

        // 주문 정보 업데이트
        Order updateOrder = Order.updateOrder(
                symbol,
                request.orderType(),
                request.orderState(),
                request.price(),
                request.quantity()
        );

        // 업데이트된 주문 저장
        Order updatedOrder = orderRepository.save(order);

        return new OrderResponseV1(
                updatedOrder.getId(),
                updatedOrder.getOrderState(),
                updatedOrder.getStatus(),
                updatedOrder.getExecutedQuantity(),
                updateOrder.getUser().getId()
        );
    }

    public void cancelOrder(Long orderId, Long userId) {
        // 주문 ID로 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 ID를 찾을 수 없습니다: " + orderId));

        // 주문 소유자 확인
        if (!order.getUser().getId().equals(userId)) {
            throw new SecurityException("해당 주문에 대한 취소 권한이 없습니다.");
        }

        // 주문 상태 확인 (체결 대기 중인 주문만 취소 가능)
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("체결 대기 중인 주문만 취소할 수 있습니다.");
        }

        // 주문 상태를 CANCELED로 변경
        Order cancelOrder = Order.cancelOrder(
                OrderStatus.CANCELED
        );

        // 변경된 주문 저장
        orderRepository.save(order);
    }

    private void validateAmount(OrderRequestV1 request) {
        String symbol = request.symbol();
        double amount = request.price() * request.quantity();
        if (symbol.endsWith("KRW") && (amount < 5000 || amount > 1_000_000_000)) {
            throw new IllegalArgumentException("KRW 주문 금액은 5,000~1,000,000,000 사이여야 합니다.");
        } else if (symbol.endsWith("USDT") && (amount < 5 || amount > 1_000_000)) {
            throw new IllegalArgumentException("USDT 주문 금액은 5~1,000,000 사이여야 합니다.");
        } else if (symbol.endsWith("BTC") && (amount < 0.00005 || amount > 10)) {
            throw new IllegalArgumentException("BTC 주문 금액은 0.00005~10 사이여야 합니다.");
        }
    }
}
