package com.example.cachingexamples.level1;

import com.example.cachingexamples.common.domain.Product;
import com.example.cachingexamples.common.dto.ProductResponse;
import com.example.cachingexamples.common.dto.ProductUpdateRequest;
import com.example.cachingexamples.common.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocalCacheProductService {

    private final ProductRepository productRepository;

    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow();
        return ProductResponse.from(product);
    }

    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest productUpdateRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow();

        product.update(productUpdateRequest.name(), productUpdateRequest.price());

        Product saved = productRepository.save(product);

        return ProductResponse.from(saved);
    }
}
