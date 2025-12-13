package com.example.cachingexamples.level1;

import com.example.cachingexamples.common.dto.ProductResponse;
import com.example.cachingexamples.common.dto.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/level1/products")
@RequiredArgsConstructor
@Slf4j
public class CacheController {
    private final CacheProductService productService;

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        ProductResponse response = productService.getProduct(id);
        long endTime = System.currentTimeMillis();
        log.info("조회 시간 : {}ms", endTime - startTime);
        return response;
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @RequestBody ProductUpdateRequest request
    ) {
        return productService.updateProduct(id, request);
    }
}
