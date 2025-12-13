package com.example.cachingexamples.level2;

import com.example.cachingexamples.common.domain.Product;
import com.example.cachingexamples.common.dto.ProductResponse;
import com.example.cachingexamples.common.dto.ProductUpdateRequest;
import com.example.cachingexamples.common.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WriteStrategyService {
    private final ProductRepository productRepository;

    /**
     * 조회 : Cache-Aside
     */
    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow();
        return ProductResponse.from(product);
    }

    /**
     * 전략 1: Write-Through
     * - DB 저장 + 캐시 갱신 동시
     */
    @CachePut(value = "products", key = "#id")
    @Transactional
    public ProductResponse updateWithWriteThrough(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow();
        product.update(request.name(), request.price());
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    /**
     * 전략 2: Write-Around + Cache Evict(가장 흔함)
     * - DB 저장 + 캐시 삭제
     */
    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public ProductResponse updateWithEvict(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow();
        product.update(request.name(), request.price());
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    /**
     * 전략 3: Write-Around
     * - DB 저장 (캐시 동작 X)
     * - 캐시 <-> DB 데이터 불일치
     */
    @Transactional
    public ProductResponse updateWithoutCache(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow();
        product.update(request.name(), request.price());
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }
}
