package com.infina.hissenet.security;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
/*
* Buradaki amac refresh token yerine kullanıcı her api isteği attığında
* token süresi 1 saat artacak uzun süre giriş yapmasa token aktifliğini yitirecek
* */
@Component
public class SessionExtensionFilter implements Filter {
    private final RedisTokenService redisTokenService;

    public SessionExtensionFilter(RedisTokenService redisTokenService) {
        this.redisTokenService = redisTokenService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String sessionId = getSessionIdFromCookie(request);
        if (sessionId != null) {
            redisTokenService.extendSession(sessionId, 3600);
        }
        chain.doFilter(request, response);
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
}
