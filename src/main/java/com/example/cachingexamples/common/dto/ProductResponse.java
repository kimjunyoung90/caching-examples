package com.example.cachingexamples.common.dto;

import com.example.cachingexamples.common.domain.Product;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProductResponse(
        Long id,
        String name,
        Integer price,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProductResponse from (Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
