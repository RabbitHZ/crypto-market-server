package com.cyptomarket.server.dto;

public record OrderHistoryDto(
        Long orderId,
        String symbol,
        String orderType,
        String orderState,
        double price,
        double quantity,
        double executedQuantity,
        String status,
        String createdAt,
        Long userId
) {}
