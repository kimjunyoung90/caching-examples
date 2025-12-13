package com.example.cachingexamples;

import com.example.cachingexamples.common.domain.Product;
import com.example.cachingexamples.common.repository.ProductRepository;
import com.example.cachingexamples.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CategoryServiceCacheTest {

    @Autowired
    CategoryService categoryService;
    @Autowired
    CacheManager cacheManager;
    @MockBean
    ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // 필요시 캐시 초기화
        if (cacheManager.getCache("localCache") != null) {
            cacheManager.getCache("localCache").clear();
        }
        if (cacheManager.getCache("redisCache") != null) {
            cacheManager.getCache("redisCache").clear();
        }
    }

    @Test
    @DisplayName("로컬 캐시 동작 테스트")
    void testLocalCache() {
        System.out.println("=== LocalCache 1st ===");
        List<Product> first = categoryService.getAllCategoriesWithLocalCache();
        System.out.println("=== LocalCache 2nd ===");
        List<Product> second = categoryService.getAllCategoriesWithLocalCache();
        // 두 번째 호출 시 DB에서 조회 로그가 출력되지 않아야 정상
    }

    @Test
    @DisplayName("Redis 캐시 동작 테스트")
    void testRedisCache() {
        System.out.println("=== RedisCache 1st ===");
        List<Product> first = categoryService.getAllCategoriesWithRedis();
        System.out.println("=== RedisCache 2nd ===");
        List<Product> second = categoryService.getAllCategoriesWithRedis();
        // 두 번째 호출 시 DB에서 조회 로그가 출력되지 않아야 정상
    }

    @Test
    @DisplayName("DB 직접 조회 테스트")
    void testDbDirect() {
        System.out.println("=== DB Direct 1st ===");
        List<Product> first = categoryService.getAllCategories();
        System.out.println("=== DB Direct 2nd ===");
        List<Product> second = categoryService.getAllCategories();
        // 두 번 모두 DB에서 조회 로그가 출력되어야 정상
    }

    @Test
    @DisplayName("로컬 캐시 동작 - Repository 1회 호출 검증")
    void testLocalCacheRepositoryCall() {
        Mockito.when(productRepository.findAll()).thenReturn(List.of());
        categoryService.getAllCategoriesWithLocalCache();
        categoryService.getAllCategoriesWithLocalCache();
        Mockito.verify(productRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Redis 캐시 동작 - Repository 1회 호출 검증")
    void testRedisCacheRepositoryCall() {
        Mockito.when(productRepository.findAll()).thenReturn(List.of());
        categoryService.getAllCategoriesWithRedis();
        categoryService.getAllCategoriesWithRedis();
        Mockito.verify(productRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("DB 직접 조회 - Repository 2회 호출 검증")
    void testDbDirectRepositoryCall() {
        Mockito.when(productRepository.findAll()).thenReturn(List.of());
        categoryService.getAllCategories();
        categoryService.getAllCategories();
        Mockito.verify(productRepository, Mockito.times(2)).findAll();
    }
}