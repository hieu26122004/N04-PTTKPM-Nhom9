package com.nhson.examservice.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("exams");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(200)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(1000));
        return cacheManager;
    }
}