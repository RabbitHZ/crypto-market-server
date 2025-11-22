package com.cyptomarket.server.dto;

import com.cyptomarket.server.entity.enums.OrderState;
import com.cyptomarket.server.entity.enums.OrderStatus;
import com.cyptomarket.server.entity.enums.OrderType;
import lombok.NonNull;

public record OrderHistoryV1(
        @NonNull Long orderId,
        @NonNull String symbol,
        @NonNull OrderType orderType,
        @NonNull OrderState orderState,
        double price,
        double quantity,
        double executedQuantity,
        @NonNull OrderStatus status,
        @NonNull String createdAt,
        @NonNull Long userId
) {}
