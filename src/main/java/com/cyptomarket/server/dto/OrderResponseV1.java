package com.cyptomarket.server.dto;

import com.cyptomarket.server.entity.enums.OrderState;
import com.cyptomarket.server.entity.enums.OrderStatus;
import lombok.NonNull;

public record OrderResponseV1(
        @NonNull Long orderId,
        @NonNull OrderState orderState,
        @NonNull OrderStatus status, // PENDING, FILLED 등
        double executedQuantity,
        @NonNull Long userId
) {}
