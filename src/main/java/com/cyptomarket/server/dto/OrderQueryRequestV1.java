package com.cyptomarket.server.dto;

import com.cyptomarket.server.entity.enums.OrderState;
import com.cyptomarket.server.entity.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrderQueryRequestV1(
        @NonNull Long userId,
        @NonNull OrderState orderState, // BUY or SELL
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate endDate,
        int page,
        int size
) {}
