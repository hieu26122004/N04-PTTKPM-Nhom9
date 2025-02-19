package com.nhson.authservice.service;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {
    private final RedisTemplate<String, Object> redisTemplate;
    public TokenBlacklistService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    public void addToBlacklist(String token, long expirationMillis) {
        String key = "blacklist:" + token;
        redisTemplate.opsForValue().set(key, "revoked", expirationMillis, TimeUnit.MILLISECONDS);
    }
    public boolean isTokenRevoked(String token) {
        String key = "blacklist:" + token;
        return redisTemplate.hasKey(key);
    }
}
