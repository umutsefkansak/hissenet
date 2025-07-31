package com.infina.hissenet.security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // sesion id -> token olarak kullancaz save
    public void saveSession(String sessionId, String token,long expirationSeconds) {
        String key="session:"+sessionId;
        redisTemplate.opsForValue().set(key,token, Duration.ofSeconds(expirationSeconds));
    }
    public String getTokenBySessionId(String sessionId) {
        String key="session:"+sessionId;
        return redisTemplate.opsForValue().get(key);
    }
    public void deleteSession(String sessionId) {
        String key="session:"+sessionId;
        redisTemplate.delete(key);
    }
}
