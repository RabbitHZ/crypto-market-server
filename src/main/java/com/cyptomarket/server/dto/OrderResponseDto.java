package com.cyptomarket.server.dto;

public record OrderResponseDto(
        Long orderId,
        String orderState,
        String status, // PENDING, FILLED 등
        double executedQuantity,
        Long userId
) {}
