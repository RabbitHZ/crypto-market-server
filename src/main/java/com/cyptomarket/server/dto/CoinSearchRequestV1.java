package com.cyptomarket.server.dto;

import org.springframework.lang.Nullable;

public record CoinSearchRequestV1(
        @Nullable String keyword, // 이름, 키워드, 초성 등
        @Nullable String category // 마켓 카테고리 (KRW, BTC, USDT)
) { }