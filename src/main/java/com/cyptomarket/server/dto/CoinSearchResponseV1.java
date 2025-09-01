package com.cyptomarket.server.dto;

import lombok.NonNull;

public record CoinSearchResponseV1(
        String name,
        String symbol,
        Double price,
        String category
) {}
