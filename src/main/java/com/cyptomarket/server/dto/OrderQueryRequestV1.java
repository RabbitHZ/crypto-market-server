package com.cyptomarket.server.dto;

import com.cyptomarket.server.entity.enums.OrderState;
import com.cyptomarket.server.entity.enums.OrderType;
import lombok.NonNull;

public record OrderQueryRequestV1(
        @NonNull Long userId,
        @NonNull String symbol, // BTC/KRW
        @NonNull OrderType orderType, // LIMIT or MARKET
        @NonNull OrderState orderState, // BUY or SELL
        double price, // 지정가 가격
        String startDate,
        String endDate
) {}
