package com.infina.hissenet.security;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Session Extension Filter - Performans Optimizasyonu ile JWT Token Auto-Refresh
 *
 * AMAÇ:
 * - Refresh token kullanmadan JWT token'ların otomatik olarak süresini uzatır
 * - Kullanıcı aktif kaldığı sürece logout olmaz, pasif kalırsa session sona erer
 *
 * ÇALIŞMA MANTĞI:
 * - Her API isteğinde sessionId cookie'sini kontrol eder
 * - Aynı session için 1 SAAT içinde sadece BİR KEZ Redis'e gider (performans)
 * - Redis'te hem session süresini hem JWT token süresini 1 saat uzatır
 * - Async thread pool kullanarak ana request akışını bloklamaz
 *
 * PERFORMANS ÖZELLİKLERİ:
 * - Memory cache ile gereksiz Redis çağrılarını engeller (%95+ Redis load azalması)
 * - Fixed thread pool (5 thread) ile resource kontrolü
 * - Non-blocking async işlemler ile hızlı response time
 *
 * ÖRNEK SENARYO:
 * 09:00 - İlk API isteği → Redis'e git, token 1 saat uzat
 * 09:15 - API isteği → Cache'den kontrol, Redis'e gitme
 * 09:30 - API isteği → Cache'den kontrol, Redis'e gitme
 * 10:00 - API isteği → 1 saat geçti, Redis'e git, token 1 saat daha uzat
 */
@Component
public class SessionExtensionFilter implements Filter {
    /**
     * Extends Redis session TTL and JWT expiration for active users
     * without blocking the request pipeline. Uses in-memory throttling
     * to limit Redis load and a small thread pool for async work.
     *
     * Author: Furkan Can
     */
    private final RedisTokenService redisTokenService;

    private final ConcurrentHashMap<String, Long> lastExtendedTimes = new ConcurrentHashMap<>();

    private static final long EXTEND_INTERVAL = 60 * 60 * 1000; // 1 saat


    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    public SessionExtensionFilter(RedisTokenService redisTokenService) {
        this.redisTokenService = redisTokenService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String sessionId = getSessionIdFromCookie(request);
        if (sessionId != null && shouldExtendSession(sessionId)) {
            extendSessionAsync(sessionId);
        }
        chain.doFilter(request, response);
    }

    private boolean shouldExtendSession(String sessionId) {
        long currentTime = System.currentTimeMillis();
        Long lastExtended = lastExtendedTimes.get(sessionId);
        return lastExtended == null || (currentTime - lastExtended) > EXTEND_INTERVAL;
    }

    private void extendSessionAsync(String sessionId) {
        lastExtendedTimes.put(sessionId, System.currentTimeMillis());
        executor.submit(() -> {
            try {
                redisTokenService.extendSession(sessionId, 3600);
            } catch (Exception e) {
                System.err.println("Session extend error for " + sessionId + ": " + e.getMessage());
                lastExtendedTimes.remove(sessionId);
            }
        });
    }

    private String getSessionIdFromCookie(ServletRequest request) {
        if (!(request instanceof HttpServletRequest)) {
            return null;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void destroy() {
        lastExtendedTimes.clear();
        executor.shutdown();
    }
}
