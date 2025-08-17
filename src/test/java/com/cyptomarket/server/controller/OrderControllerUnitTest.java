package com.cyptomarket.server.controller;

import com.cyptomarket.server.dto.OrderQueryRequestV1;
import com.cyptomarket.server.dto.OrderRequestV1;
import com.cyptomarket.server.entity.enums.OrderState;
import com.cyptomarket.server.entity.enums.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class OrderControllerUnitTest {

    @Autowired
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        orderController = new OrderController() {
            // getAuthenticatedUserId를 오버라이드해서 테스트용 Mock userId 반환
            protected Long getAuthenticatedUserId() {
                return 1L;
            }
        };
    }

    @Test
    @DisplayName("주문 생성 실패 - 수량이 0이면 예외 발생")
    void createOrder_invalidQuantity_shouldThrow() {
        OrderRequestV1 request = new OrderRequestV1(
                "BTC/KRW",
                OrderType.LIMIT,
                OrderState.BUY,
                50000000,
                0.0 // 잘못된 수량
        );

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> orderController.createOrder(request));

        assertEquals("음수 수량 또는 0은 허용되지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("주문 수정 실패 - 수량이 0이면 예외 발생")
    void updateOrder_notFound_shouldThrow() {
        OrderRequestV1 request = new OrderRequestV1(
                "BTC/KRW",
                OrderType.LIMIT,
                OrderState.BUY,
                50000000,
                0.0 // 잘못된 수량
        );

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> orderController.createOrder(request));

        assertEquals("음수 수량 또는 0은 허용되지 않습니다.", exception.getMessage());
    }
}