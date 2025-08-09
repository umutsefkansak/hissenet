package com.infina.hissenet.security;

import com.infina.hissenet.security.abstracts.IRedisTokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisTokenService implements IRedisTokenService {
    /**
     * Manages session tokens in Redis and coordinates TTL extension
     * by leveraging JWT regeneration with preserved claims.
     *
     * Author: Furkan Can
     */
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;

    public RedisTokenService(RedisTemplate<String, String> redisTemplate, JwtService jwtService) {
        this.redisTemplate = redisTemplate;
        this.jwtService = jwtService;
    }

    // sesion id -> token olarak kullancaz save
    public void saveSession(String sessionId, String token, long expirationSeconds) {
        String key = "session:" + sessionId;
        redisTemplate.opsForValue().set(key, token, Duration.ofSeconds(expirationSeconds));
    }

    public String getTokenBySessionId(String sessionId) {
        String key = "session:" + sessionId;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteSession(String sessionId) {
        String key = "session:" + sessionId;
        redisTemplate.delete(key);
    }

    public void extendSession(String sessionId, long extensionSeconds) {
        String key = "session:" + sessionId;
        String oldToken = redisTemplate.opsForValue().get(key);
        if (oldToken != null) {
            String newToken = jwtService.refreshTokenExpiration(oldToken);
            Long currentTtl = redisTemplate.getExpire(key);
            if (currentTtl != null && currentTtl > 0) {
                redisTemplate.opsForValue().set(key, newToken, Duration.ofSeconds(currentTtl + extensionSeconds));
            } else {
                redisTemplate.opsForValue().set(key, newToken, Duration.ofSeconds(extensionSeconds));
            }
        }
    }
}