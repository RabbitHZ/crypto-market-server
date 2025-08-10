package com.cyptomarket.server.dto;

public record OrderQueryRequestDto(
        Long userId,
        String symbol, // BTC/KRW
        String orderType, // LIMIT or MARKET
        String orderState, // BUY or SELL
        double price // 지정가 가격
) {}
