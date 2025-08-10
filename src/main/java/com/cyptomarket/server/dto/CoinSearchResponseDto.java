package com.cyptomarket.server.dto;

public record CoinSearchResponseDto(
        String symbol, // BTC/KRW
        String baseCoin, // BTC
        double currentPrice, // 현재가
        double openPrice, // 시가
        double closePrice, // 종가
        double highPrice, // 고가
        double lowPrice, // 저가
        double volume // 거래량
) {}
