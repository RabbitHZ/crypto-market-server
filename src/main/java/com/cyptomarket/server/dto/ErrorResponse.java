package com.cyptomarket.server.dto;

public record ErrorResponse (
        String error,
        String message
) {}
