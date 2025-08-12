package com.infina.hissenet.interceptor;

import com.infina.hissenet.config.RateLimitConfig;
import com.infina.hissenet.exception.common.RateLimitException;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Intercepts incoming HTTP requests and enforces per-client rate limiting
 * using Bucket4j token buckets. Adds remaining limit information to
 * response headers and throws a domain-specific exception when limits are
 * exceeded.
 *
 * Responsibilities:
 * - Create and cache token buckets per client IP
 * - Consume a token for each request and expose remaining tokens
 * - Signal rate limit exceedance via {@link RateLimitException}
 *
 * Author: Furkan Can
 */
@Configuration
public class RateLimitInterceptor implements HandlerInterceptor {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final RateLimitConfig rateLimitConfig;

    public RateLimitInterceptor(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }
    @Override
    /**
     * Applies rate limiting before controller execution.
     * @return true if request is allowed, otherwise throws {@link RateLimitException}
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ipAddress = getClientIP(request);


        Bucket bucket = buckets.computeIfAbsent(ipAddress, k -> rateLimitConfig.createNewBucket());


        if (bucket.tryConsume(1)) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(bucket.getAvailableTokens()));
            return true;
        }


        throw new RateLimitException();

    }

    /**
     * Determines the client IP using X-Forwarded-For when present,
     * falling back to the remote address.
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
