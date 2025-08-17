package com.cyptomarket.server.dto;

import com.cyptomarket.server.entity.enums.OrderState;
import com.cyptomarket.server.entity.enums.OrderType;
import lombok.NonNull;

public record OrderRequestV1(
        @NonNull String symbol,
        @NonNull OrderType orderType,
        @NonNull OrderState orderState,
        double price,
        double quantity
) {}
