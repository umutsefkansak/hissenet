package com.infina.hissenet.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisTokenService Unit Tests")
class RedisTokenServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private JwtService jwtService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisTokenService redisTokenService;

    private String testSessionId;
    private String testToken;
    private long testExpirationSeconds;

    @BeforeEach
    void setUp() {
        // Arrange - Test data setup
        testSessionId = "test-session-123";
        testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNjE2MTYxNjE2LCJleHAiOjE2MTYxNjUyMTZ9.testSignature";
        testExpirationSeconds = 3600L; // 1 hour

        // Setup RedisTemplate mock
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("saveSession - Should save session with token and expiration")
    void saveSession_ShouldSaveSessionWithTokenAndExpiration() {
        // Arrange
        String expectedKey = "session:" + testSessionId;
        Duration expectedDuration = Duration.ofSeconds(testExpirationSeconds);

        // Act
        redisTokenService.saveSession(testSessionId, testToken, testExpirationSeconds);

        // Assert
        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(expectedKey, testToken, expectedDuration);
    }

    @Test
    @DisplayName("saveSession - Should handle zero expiration")
    void saveSession_ShouldHandleZeroExpiration() {
        // Arrange
        String expectedKey = "session:" + testSessionId;
        Duration expectedDuration = Duration.ofSeconds(0);

        // Act
        redisTokenService.saveSession(testSessionId, testToken, 0);

        // Assert
        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(expectedKey, testToken, expectedDuration);
    }

    @Test
    @DisplayName("saveSession - Should handle negative expiration")
    void saveSession_ShouldHandleNegativeExpiration() {
        // Arrange
        String expectedKey = "session:" + testSessionId;
        Duration expectedDuration = Duration.ofSeconds(-3600);

        // Act
        redisTokenService.saveSession(testSessionId, testToken, -3600);

        // Assert
        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(expectedKey, testToken, expectedDuration);
    }

    @Test
    @DisplayName("getTokenBySessionId - Should return token when session exists")
    void getTokenBySessionId_ShouldReturnToken_WhenSessionExists() {
        // Arrange
        String expectedKey = "session:" + testSessionId;
        when(valueOperations.get(expectedKey)).thenReturn(testToken);

        // Act
        String result = redisTokenService.getTokenBySessionId(testSessionId);

        // Assert
        assertNotNull(result);
        assertEquals(testToken, result);
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(expectedKey);
    }

    @Test
    @DisplayName("getTokenBySessionId - Should return null when session does not exist")
    void getTokenBySessionId_ShouldReturnNull_WhenSessionDoesNotExist() {
        // Arrange
        String expectedKey = "session:" + testSessionId;
        when(valueOperations.get(expectedKey)).thenReturn(null);

        // Act
        String result = redisTokenService.getTokenBySessionId(testSessionId);

        // Assert
        assertNull(result);
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(expectedKey);
    }



    @Test
    @DisplayName("extendSession - Should extend session when session exists and has TTL")
    void extendSession_ShouldExtendSession_WhenSessionExistsAndHasTtl() {
        // Arrange
        String expectedKey = "session:" + testSessionId;
        String newToken = "new.refreshed.token";
        Long currentTtl = 1800L; // 30 minutes remaining
        long extensionSeconds = 3600L; // 1 hour extension

        when(valueOperations.get(expectedKey)).thenReturn(testToken);
        when(redisTemplate.getExpire(expectedKey)).thenReturn(currentTtl);
        when(jwtService.refreshTokenExpiration(testToken)).thenReturn(newToken);

        // Act
        redisTokenService.extendSession(testSessionId, extensionSeconds);

        // Assert

        verify(valueOperations).get(expectedKey);
        verify(redisTemplate).getExpire(expectedKey);
        verify(jwtService).refreshTokenExpiration(testToken);
        verify(valueOperations).set(expectedKey, newToken, Duration.ofSeconds(currentTtl + extensionSeconds));
    }

    @Test
    @DisplayName("extendSession - Should extend session when session exists but no TTL")
    void extendSession_ShouldExtendSession_WhenSessionExistsButNoTtl() {
        // Arrange
        String expectedKey = "session:" + testSessionId;
        String newToken = "new.refreshed.token";
        long extensionSeconds = 3600L; // 1 hour extension

        when(valueOperations.get(expectedKey)).thenReturn(testToken);
        when(redisTemplate.getExpire(expectedKey)).thenReturn(null);
        when(jwtService.refreshTokenExpiration(testToken)).thenReturn(newToken);

        // Act
        redisTokenService.extendSession(testSessionId, extensionSeconds);

        // Assert
        verify(valueOperations).get(expectedKey);
        verify(redisTemplate).getExpire(expectedKey);
        verify(jwtService).refreshTokenExpiration(testToken);
        verify(valueOperations).set(expectedKey, newToken, Duration.ofSeconds(extensionSeconds));
    }

    @Test
    @DisplayName("extendSession - Should handle when session does not exist")
    void extendSession_ShouldHandle_WhenSessionDoesNotExist() {
        // Arrange
        String expectedKey = "session:" + testSessionId;
        long extensionSeconds = 3600L;

        when(valueOperations.get(expectedKey)).thenReturn(null);

        // Act
        redisTokenService.extendSession(testSessionId, extensionSeconds);

        // Assert
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(expectedKey);
        verify(redisTemplate, never()).getExpire(anyString());
        verify(jwtService, never()).refreshTokenExpiration(anyString());
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    @DisplayName("extendSession - Should handle when TTL is zero")
    void extendSession_ShouldHandle_WhenTtlIsZero() {
        // Arrange
        String expectedKey = "session:" + testSessionId;
        String newToken = "new.refreshed.token";
        long extensionSeconds = 3600L;

        when(valueOperations.get(expectedKey)).thenReturn(testToken);
        when(redisTemplate.getExpire(expectedKey)).thenReturn(0L);
        when(jwtService.refreshTokenExpiration(testToken)).thenReturn(newToken);

        // Act
        redisTokenService.extendSession(testSessionId, extensionSeconds);

        // Assert
        verify(valueOperations).get(expectedKey);
        verify(redisTemplate).getExpire(expectedKey);
        verify(jwtService).refreshTokenExpiration(testToken);
        verify(valueOperations).set(expectedKey, newToken, Duration.ofSeconds(extensionSeconds));
    }

    @Test
    @DisplayName("extendSession - Should handle when TTL is negative")
    void extendSession_ShouldHandle_WhenTtlIsNegative() {
        // Arrange
        String expectedKey = "session:" + testSessionId;
        String newToken = "new.refreshed.token";
        long extensionSeconds = 3600L;

        when(valueOperations.get(expectedKey)).thenReturn(testToken);
        when(redisTemplate.getExpire(expectedKey)).thenReturn(-1L);
        when(jwtService.refreshTokenExpiration(testToken)).thenReturn(newToken);

        // Act
        redisTokenService.extendSession(testSessionId, extensionSeconds);

        // Assert

        verify(valueOperations).get(expectedKey);
        verify(redisTemplate).getExpire(expectedKey);
        verify(jwtService).refreshTokenExpiration(testToken);
        verify(valueOperations).set(expectedKey, newToken, Duration.ofSeconds(extensionSeconds));
    }

    @Test
    @DisplayName("extendSession - Should handle when jwtService returns same token")
    void extendSession_ShouldHandle_WhenJwtServiceReturnsSameToken() {
        // Arrange
        String expectedKey = "session:" + testSessionId;
        Long currentTtl = 1800L;
        long extensionSeconds = 3600L;

        when(valueOperations.get(expectedKey)).thenReturn(testToken);
        when(redisTemplate.getExpire(expectedKey)).thenReturn(currentTtl);
        when(jwtService.refreshTokenExpiration(testToken)).thenReturn(testToken); // Same token returned

        // Act
        redisTokenService.extendSession(testSessionId, extensionSeconds);

        // Assert
        verify(valueOperations).get(expectedKey);
        verify(redisTemplate).getExpire(expectedKey);
        verify(jwtService).refreshTokenExpiration(testToken);
        verify(valueOperations).set(expectedKey, testToken, Duration.ofSeconds(currentTtl + extensionSeconds));
    }

    @Test
    @DisplayName("saveSession - Should handle null sessionId")
    void saveSession_ShouldHandleNullSessionId() {
        // Arrange
        String nullSessionId = null;
        String expectedKey = "session:null";

        // Act
        redisTokenService.saveSession(nullSessionId, testToken, testExpirationSeconds);

        // Assert
        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(expectedKey, testToken, Duration.ofSeconds(testExpirationSeconds));
    }

    @Test
    @DisplayName("saveSession - Should handle null token")
    void saveSession_ShouldHandleNullToken() {
        // Arrange
        String nullToken = null;
        String expectedKey = "session:" + testSessionId;

        // Act
        redisTokenService.saveSession(testSessionId, nullToken, testExpirationSeconds);

        // Assert
        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(expectedKey, nullToken, Duration.ofSeconds(testExpirationSeconds));
    }

    @Test
    @DisplayName("getTokenBySessionId - Should handle null sessionId")
    void getTokenBySessionId_ShouldHandleNullSessionId() {
        // Arrange
        String nullSessionId = null;
        String expectedKey = "session:null";
        when(valueOperations.get(expectedKey)).thenReturn(testToken);

        // Act
        String result = redisTokenService.getTokenBySessionId(nullSessionId);

        // Assert
        assertNotNull(result);
        assertEquals(testToken, result);
        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(expectedKey);
    }



    @Test
    @DisplayName("extendSession - Should handle null sessionId")
    void extendSession_ShouldHandleNullSessionId() {
        // Arrange
        String nullSessionId = null;
        String expectedKey = "session:null";
        long extensionSeconds = 3600L;

        when(valueOperations.get(expectedKey)).thenReturn(testToken);
        when(redisTemplate.getExpire(expectedKey)).thenReturn(1800L);
        when(jwtService.refreshTokenExpiration(testToken)).thenReturn(testToken);

        // Act
        redisTokenService.extendSession(nullSessionId, extensionSeconds);

        // Assert

        verify(valueOperations).get(expectedKey);
        verify(redisTemplate).getExpire(expectedKey);
        verify(jwtService).refreshTokenExpiration(testToken);
    }
} 