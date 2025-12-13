package com.example.cachingexamples.service;

import com.example.cachingexamples.common.domain.Product;
import com.example.cachingexamples.common.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProductRepository productRepository;

    public List<Product> getAllCategories() {
        List<Product> categories = productRepository.findAll();
        return categories;
    }

    @Cacheable(cacheNames = "localCache", key = "'category'")
    public List<Product> getAllCategoriesWithLocalCache() {
        System.out.println("DB에서 조회"); // 로그 찍히면 캐시 안 된 것
        return productRepository.findAll();
    }

    @Cacheable(cacheNames = "redisCache", key = "'category'")
    public List<Product> getAllCategoriesWithRedis() {
        System.out.println("DB에서 조회 (Redis)");
        return productRepository.findAll();
    }

}
