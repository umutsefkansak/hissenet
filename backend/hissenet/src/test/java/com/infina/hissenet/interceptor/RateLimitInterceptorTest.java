package com.infina.hissenet.interceptor;

import com.infina.hissenet.config.RateLimitConfig;
import com.infina.hissenet.exception.common.RateLimitException;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimitInterceptor Unit Tests")
class RateLimitInterceptorTest {

    @Mock
    private RateLimitConfig rateLimitConfig;

    @Mock
    private Bucket bucket;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private RateLimitInterceptor rateLimitInterceptor;

    private String testIpAddress;

    @BeforeEach
    void setUp() {
        testIpAddress = "192.168.1.100";
        when(request.getRemoteAddr()).thenReturn(testIpAddress);
        when(rateLimitConfig.createNewBucket()).thenReturn(bucket);
    }

    @Test
    @DisplayName("preHandle - Should allow request when tokens available")
    void preHandle_ShouldAllowRequest_WhenTokensAvailable() throws Exception {
        // Arrange
        when(bucket.tryConsume(1)).thenReturn(true);
        when(bucket.getAvailableTokens()).thenReturn(99L);

        // Act
        boolean result = rateLimitInterceptor.preHandle(request, response, null);

        // Assert
        assertTrue(result);
        verify(response).addHeader("X-Rate-Limit-Remaining", "99");
        verify(bucket).tryConsume(1);
    }

    @Test
    @DisplayName("preHandle - Should throw RateLimitException when no tokens available")
    void preHandle_ShouldThrowRateLimitException_WhenNoTokensAvailable() {
        // Arrange
        when(bucket.tryConsume(1)).thenReturn(false);

        // Act & Assert
        assertThrows(RateLimitException.class, () -> {
            rateLimitInterceptor.preHandle(request, response, null);
        });
        verify(bucket).tryConsume(1);
        verify(response, never()).addHeader(anyString(), anyString());
    }

    @Test
    @DisplayName("preHandle - Should create new bucket for new IP address")
    void preHandle_ShouldCreateNewBucket_ForNewIpAddress() throws Exception {
        // Arrange
        String newIpAddress = "10.0.0.1";
        when(request.getRemoteAddr()).thenReturn(newIpAddress);
        when(bucket.tryConsume(1)).thenReturn(true);
        when(bucket.getAvailableTokens()).thenReturn(100L);

        // Act
        boolean result = rateLimitInterceptor.preHandle(request, response, null);

        // Assert
        assertTrue(result);
        verify(rateLimitConfig).createNewBucket();
        verify(bucket).tryConsume(1);
    }

    @Test
    @DisplayName("preHandle - Should reuse existing bucket for same IP address")
    void preHandle_ShouldReuseExistingBucket_ForSameIpAddress() throws Exception {
        // Arrange
        when(bucket.tryConsume(1)).thenReturn(true);
        when(bucket.getAvailableTokens()).thenReturn(98L);

        // Act - First call
        boolean result1 = rateLimitInterceptor.preHandle(request, response, null);
        
        // Act - Second call
        boolean result2 = rateLimitInterceptor.preHandle(request, response, null);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(rateLimitConfig, times(1)).createNewBucket(); // Should only be called once
        verify(bucket, times(2)).tryConsume(1);
    }




    @Test
    @DisplayName("preHandle - Should fallback to remote address when X-Forwarded-For is null")
    void preHandle_ShouldFallbackToRemoteAddress_WhenXForwardedForIsNull() throws Exception {
        // Arrange
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(testIpAddress);
        when(bucket.tryConsume(1)).thenReturn(true);
        when(bucket.getAvailableTokens()).thenReturn(100L);

        // Act
        boolean result = rateLimitInterceptor.preHandle(request, response, null);

        // Assert
        assertTrue(result);
        verify(request).getHeader("X-Forwarded-For");
        verify(request).getRemoteAddr();
        verify(rateLimitConfig).createNewBucket();
    }



    @Test
    @DisplayName("preHandle - Should handle different IP addresses separately")
    void preHandle_ShouldHandleDifferentIpAddressesSeparately() throws Exception {
        // Arrange
        String ip1 = "192.168.1.1";
        String ip2 = "192.168.1.2";
        
        when(request.getRemoteAddr()).thenReturn(ip1);
        when(bucket.tryConsume(1)).thenReturn(true);
        when(bucket.getAvailableTokens()).thenReturn(100L);

        // Act - First IP
        boolean result1 = rateLimitInterceptor.preHandle(request, response, null);
        
        // Change IP
        when(request.getRemoteAddr()).thenReturn(ip2);
        
        // Act - Second IP
        boolean result2 = rateLimitInterceptor.preHandle(request, response, null);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(rateLimitConfig, times(2)).createNewBucket(); // Should be called twice for different IPs
    }

    @Test
    @DisplayName("preHandle - Should handle concurrent requests for same IP")
    void preHandle_ShouldHandleConcurrentRequestsForSameIp() throws Exception {
        // Arrange
        when(bucket.tryConsume(1)).thenReturn(true);
        when(bucket.getAvailableTokens()).thenReturn(99L, 98L, 97L);

        // Act - Multiple concurrent calls
        boolean result1 = rateLimitInterceptor.preHandle(request, response, null);
        boolean result2 = rateLimitInterceptor.preHandle(request, response, null);
        boolean result3 = rateLimitInterceptor.preHandle(request, response, null);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
        verify(bucket, times(3)).tryConsume(1);
        verify(rateLimitConfig, times(1)).createNewBucket(); // Should only be called once
    }

    @Test
    @DisplayName("preHandle - Should handle rate limit exceeded scenario")
    void preHandle_ShouldHandleRateLimitExceededScenario() {
        // Arrange
        when(bucket.tryConsume(1)).thenReturn(false);

        // Act & Assert
        RateLimitException exception = assertThrows(RateLimitException.class, () -> {
            rateLimitInterceptor.preHandle(request, response, null);
        });

        assertNotNull(exception.getMessage());
        verify(bucket).tryConsume(1);
        verify(response, never()).addHeader(anyString(), anyString());
    }

    @Test
    @DisplayName("preHandle - Should handle null handler parameter")
    void preHandle_ShouldHandleNullHandlerParameter() throws Exception {
        // Arrange
        when(bucket.tryConsume(1)).thenReturn(true);
        when(bucket.getAvailableTokens()).thenReturn(100L);

        // Act
        boolean result = rateLimitInterceptor.preHandle(request, response, null);

        // Assert
        assertTrue(result);
        verify(bucket).tryConsume(1);
        verify(response).addHeader("X-Rate-Limit-Remaining", "100");
    }
} 