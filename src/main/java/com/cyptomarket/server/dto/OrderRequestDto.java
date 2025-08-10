package com.cyptomarket.server.dto;
public record OrderRequestDto(
        String symbol,
        String orderType,
        String orderState,
        double price,
        double quantity
) {}
