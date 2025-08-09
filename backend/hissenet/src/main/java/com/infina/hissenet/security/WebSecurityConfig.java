package com.infina.hissenet.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



/**
 * HTTP security configuration for the application.
 *
 * Responsibilities:
 * - Enables stateless sessions for JWT-based authentication
 * - Configures exception handling with RFC 7807 Problem Details responses
 * - Whitelists public endpoints (auth, swagger, websockets)
 * - Registers token and session-extension filters
 *
 * Author: Furkan Can
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    private final TokenFilter tokenFilter;
    private final SessionExtensionFilter sessionExtensionFilter;

    public WebSecurityConfig(TokenFilter tokenFilter, SessionExtensionFilter sessionExtensionFilter) {
        this.tokenFilter = tokenFilter;
        this.sessionExtensionFilter = sessionExtensionFilter;
    }



    @Bean
    /**
     * Builds the security filter chain including authorization rules,
     * exception handling, and custom filters for token validation and
     * session extension.
     */
    SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/problem+json");
                            response.getWriter().write("""
                    {
                        "type": "https://hissenet.com/problems/authentication",
                        "title": "Unauthorized",
                        "status": 401,
                        "detail": "You must be authenticated to access this resource.",
                        "instance": "%s"
                    }
                    """.formatted(request.getRequestURI()));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/problem+json");
                            response.getWriter().write("""
                    {
                        "type": "https://hissenet.com/problems/authorization",
                        "title": "Forbidden",
                        "status": 403,
                        "detail": "You are not authorized to access this resource.",
                        "instance": "%s"
                    }
                    """.formatted(request.getRequestURI()));
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/employees").permitAll()
                        .requestMatchers(HttpMethod.PATCH,"/api/v1/employees/changePassword").permitAll()
                        .requestMatchers("/api/v1/mail/verify","/api/v1/mail/send-verification","/api/v1/mail/send-password-reset","/api/v1/mail/send-password-change-token","/api/v1/mail/verify-password-change-token").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/ws-stock/**","/ws/**", "/websocket/**", "/stomp/**").permitAll() // WebSocket endpoints
                        .anyRequest().authenticated()
                )
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(sessionExtensionFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}