package com.infina.hissenet.security;

import com.infina.hissenet.entity.Employee;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class Filter extends OncePerRequestFilter{
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;

    public Filter(JwtService jwtService, RedisTokenService redisTokenService) {
        this.jwtService = jwtService;
        this.redisTokenService = redisTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String sessionId=getSessionIdFromCookie(request);
        // validate token ederken user var mı yok mu kontrol ediyorum
        if(sessionId!=null) {
            String token=redisTokenService.getTokenBySessionId(sessionId);
            if(token!=null) {
                if(jwtService.validateToken(token)) {
                    Employee user=jwtService.getUser(token);
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }


        }
        filterChain.doFilter(request, response);
    }
    private String getSessionIdFromCookie(HttpServletRequest request){
        var cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies){
                if(cookie.getName().equals("sessionId") && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}