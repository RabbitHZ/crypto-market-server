package com.cyptomarket.server.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.Nullable;

public record CoinSearchRequestV1(
        @NotBlank(message = "검색어가 비어있습니다.")
        String keyword, // 이름, 키워드, 초성 등
        @Nullable String category // 마켓 카테고리 (KRW, BTC, USDT)
) { }