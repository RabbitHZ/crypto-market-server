package com.cyptomarket.server.dto;

import com.cyptomarket.server.entity.enums.OrderState;
import com.cyptomarket.server.entity.enums.OrderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.NonNull;

public record OrderRequestV1(
        @NotBlank(message = "종목 코드는 필수입니다.")
        String symbol,

        @NotNull(message = "주문 유형은 필수입니다.")
        OrderType orderType,

        @NotNull(message = "주문 상태는 필수입니다.")
        OrderState orderState,

        @Positive(message = "가격은 0 이상이어야 합니다.")
        double price,

        @Positive(message = "수량은 0보다 커야 합니다.")
        double quantity
) {}
