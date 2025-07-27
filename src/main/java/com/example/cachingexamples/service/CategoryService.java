package com.example.cachingexamples.service;

import com.example.cachingexamples.domain.Category;
import com.example.cachingexamples.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories;
    }

    @Cacheable(cacheNames = "localCache", key = "'category'")
    public List<Category> getAllCategoriesWithLocalCache() {
        System.out.println("DB에서 조회"); // 로그 찍히면 캐시 안 된 것
        return categoryRepository.findAll();
    }

    @Cacheable(cacheNames = "localCache", key = "'category'")
    public List<Category> getAllCategoriesWithRedis() {
        System.out.println("DB에서 조회"); // 로그 찍히면 캐시 안 된 것
        return categoryRepository.findAll();
    }

}
