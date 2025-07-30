package com.example.cachingexamples.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@EnableCaching
@Configuration
public class CacheConfig {

    // @Bean
    // public CaffeineCacheManager cacheManager() {
    //     CaffeineCacheManager cacheManager = new CaffeineCacheManager("localCache");
    //     cacheManager.setCaffeine(
    //             Caffeine.newBuilder()
    //                     .maximumSize(10000)
    //                     .expireAfterWrite(Duration.ofMinutes(10)) // 10분
    //     );
    //     return cacheManager;
    // }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)); // 10분
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
