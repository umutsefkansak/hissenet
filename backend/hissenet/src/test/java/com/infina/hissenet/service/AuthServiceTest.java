package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.LoginRequest;
import com.infina.hissenet.dto.response.AuthResponse;
import com.infina.hissenet.dto.response.UserResponse;
import com.infina.hissenet.entity.Employee;
import com.infina.hissenet.exception.auth.LoginException;
import com.infina.hissenet.mapper.EmployeeMapper;
import com.infina.hissenet.security.JwtService;
import com.infina.hissenet.security.RedisTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private EmployeeService employeeService;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmployeeMapper employeeMapper;
    @Mock private RedisTokenService redisTokenService;

    @InjectMocks private AuthService authService;

    @Captor private ArgumentCaptor<String> sessionIdCaptor;
    @Captor private ArgumentCaptor<String> tokenCaptor;
    @Captor private ArgumentCaptor<Long> expirationCaptor;

    private static final String EMAIL = "user@example.com";
    private static final String RAW_PASSWORD = "Secret123";
    private static final String ENCODED_PASSWORD = "$2a$10$encoded";
    private static final String JWT_TOKEN = "jwt-token";
    private static final long ONE_WEEK_SECONDS = 7L * 24 * 60 * 60; // 604800

    private Employee employee;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setEmail(EMAIL);
        employee.setPassword(ENCODED_PASSWORD);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        userResponse = new UserResponse(null, "John", "Doe", EMAIL, null, null, java.util.Set.of(), null, null);
    }

    @Test
    void login_success_returnsAuthResponseAndPersistsSession() {
        // Arrange
        LoginRequest request = new LoginRequest(EMAIL, RAW_PASSWORD);
        when(employeeService.findByEmail(EMAIL)).thenReturn(employee);
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtService.generateJwtToken(EMAIL)).thenReturn(JWT_TOKEN);
        when(employeeMapper.toUserResponse(employee)).thenReturn(userResponse);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response, "AuthResponse should not be null");
        assertEquals(EMAIL, response.response().email());
        assertNotNull(response.sessionId(), "Session id should be generated");
        assertFalse(response.sessionId().isEmpty(), "Session id should not be empty");
        assertEquals(ONE_WEEK_SECONDS, response.time(), "TTL should be 1 week in seconds");

        // Verify interactions
        verify(jwtService).generateJwtToken(EMAIL);

        // Capture saveSession args to ensure correct TTL and same session id used
        verify(redisTokenService).saveSession(sessionIdCaptor.capture(), tokenCaptor.capture(), expirationCaptor.capture());
        assertEquals(response.sessionId(), sessionIdCaptor.getValue());
        assertEquals(JWT_TOKEN, tokenCaptor.getValue());
        assertEquals(ONE_WEEK_SECONDS, expirationCaptor.getValue());
    }

    @Test
    void login_wrongPassword_throwsLoginException() {
        // Arrange
        LoginRequest request = new LoginRequest(EMAIL, RAW_PASSWORD);
        when(employeeService.findByEmail(EMAIL)).thenReturn(employee);
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        // Act + Assert
        assertThrows(LoginException.class, () -> authService.login(request));

        verify(jwtService, never()).generateJwtToken(anyString());
        verify(redisTokenService, never()).saveSession(anyString(), anyString(), anyLong());
    }

    @Test
    void logout_deletesSessionAndReturnsMessage() {
        // Arrange
        String sessionId = "session-123";

        // Act
        String result = authService.logout(sessionId);

        // Assert
        assertEquals("You have been logged out", result);
        verify(redisTokenService).deleteSession(sessionId);
    }
}


