package com.cyptomarket.server.dto;

public record CoinSearchRequestDto(
        String keyword, // 이름, 키워드, 초성 등
        String category // 마켓 카테고리 (KRW, BTC, USDT)
) { }