package com.example.cachingexamples.common.dto;

public record ProductUpdateRequest(
        String name,
        Integer price
) {
}
