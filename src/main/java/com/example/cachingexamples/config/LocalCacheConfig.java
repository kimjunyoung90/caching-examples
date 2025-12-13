package com.example.cachingexamples.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@EnableCaching
@Profile("local")
@Configuration
public class LocalCacheConfig {
     @Bean
     public CaffeineCacheManager cacheManager() {
         CaffeineCacheManager cacheManager = new CaffeineCacheManager("localCache");
         cacheManager.setCaffeine(
                 Caffeine.newBuilder()
                         .maximumSize(10000)
                         .expireAfterWrite(Duration.ofMinutes(10)) // 10분
         );
         return cacheManager;
     }
}
