package com.example.cachingexamples.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Profile("local")
@EnableCaching
@Configuration
public class LocalCacheConfig {
     @Bean
     public CacheManager cacheManager() {

         CaffeineCache caffeineCache = new CaffeineCache(
                 "products", //@Cacheable에서 사용할 캐시 이름
                 Caffeine.newBuilder()
                         .expireAfterWrite(5, TimeUnit.MINUTES)
                         .maximumSize(100)
                         .build()
         );

         //SimpleCacheManager에 등록한 캐시를 등록
         SimpleCacheManager cacheManager = new SimpleCacheManager();
         cacheManager.setCaches(Arrays.asList(caffeineCache));
         return cacheManager;
     }
}
