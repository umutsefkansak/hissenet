package com.infina.hissenet.security;

import com.infina.hissenet.entity.Employee;
import com.infina.hissenet.entity.Role;
import com.infina.hissenet.service.EmployeeService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private JwtService jwtService;

    private Employee testEmployee;
    private String testEmail;
    private String validToken;
    private String expiredToken;

    @BeforeEach
    void setUp() {
        // Arrange - Test data setup
        testEmail = "test@example.com";
        
        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setEmail(testEmail);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setPassword("encodedPassword");
        
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        roles.add(role);
        testEmployee.setRoles(roles);

        // Set JWT properties using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "JWT_SECRET", "testSecretKeyForJwtTokenGenerationAndValidation");
        ReflectionTestUtils.setField(jwtService, "EXPIRATION_TIME", 3600000L); // 1 hour

        // Generate valid token for testing
        validToken = generateTestToken(testEmail, System.currentTimeMillis() + 3600000);
        expiredToken = generateTestToken(testEmail, System.currentTimeMillis() - 3600000);
    }

    @Test
    @DisplayName("generateJwtToken - Should generate valid JWT token")
    void generateJwtToken_ShouldGenerateValidJwtToken() {
        // Arrange
        String email = "test@example.com";

        // Act
        String token = jwtService.generateJwtToken(email);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token structure
        Claims claims = Jwts.parserBuilder()
                .setSigningKey("testSecretKeyForJwtTokenGenerationAndValidation")
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        assertEquals(email, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    @DisplayName("generateJwtToken - Should generate token with correct expiration")
    void generateJwtToken_ShouldGenerateTokenWithCorrectExpiration() {
        // Arrange
        String email = "test@example.com";
        long currentTime = System.currentTimeMillis();

        // Act
        String token = jwtService.generateJwtToken(email);

        // Assert
        Claims claims = Jwts.parserBuilder()
                .setSigningKey("testSecretKeyForJwtTokenGenerationAndValidation")
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        // Token should expire in 1 hour (3600000ms)
        long expectedExpiration = currentTime + 3600000L;
        long actualExpiration = claims.getExpiration().getTime();
        
        // Allow 5 seconds tolerance for test execution time
        assertTrue(Math.abs(expectedExpiration - actualExpiration) < 5000);
    }

    @Test
    @DisplayName("getUser - Should return employee when valid token provided")
    void getUser_ShouldReturnEmployee_WhenValidTokenProvided() {
        // Arrange
        when(employeeService.findByEmailWithRoles(testEmail)).thenReturn(testEmployee);

        // Act
        Employee result = jwtService.getUser(validToken);

        // Assert
        assertNotNull(result);
        assertEquals(testEmail, result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(employeeService).findByEmailWithRoles(testEmail);
    }

    @Test
    @DisplayName("getUser - Should return null when employee not found")
    void getUser_ShouldReturnNull_WhenEmployeeNotFound() {
        // Arrange
        when(employeeService.findByEmailWithRoles(testEmail)).thenReturn(null);

        // Act
        Employee result = jwtService.getUser(validToken);

        // Assert
        assertNull(result);
        verify(employeeService).findByEmailWithRoles(testEmail);
    }

    @Test
    @DisplayName("validateToken - Should return true for valid token")
    void validateToken_ShouldReturnTrue_ForValidToken() {
        // Arrange
        when(employeeService.findByEmailWithRoles(testEmail)).thenReturn(testEmployee);

        // Act
        boolean result = jwtService.validateToken(validToken);

        // Assert
        assertTrue(result);
        verify(employeeService).findByEmailWithRoles(testEmail);
    }



    @Test
    @DisplayName("validateToken - Should return false when employee not found")
    void validateToken_ShouldReturnFalse_WhenEmployeeNotFound() {
        // Arrange
        when(employeeService.findByEmailWithRoles(testEmail)).thenReturn(null);

        // Act
        boolean result = jwtService.validateToken(validToken);

        // Assert
        assertFalse(result);
        verify(employeeService).findByEmailWithRoles(testEmail);
    }



    @Test
    @DisplayName("getUser - Should handle token with different email")
    void getUser_ShouldHandleTokenWithDifferentEmail() {
        // Arrange
        String differentEmail = "different@example.com";
        String tokenWithDifferentEmail = generateTestToken(differentEmail, System.currentTimeMillis() + 3600000);
        
        Employee differentEmployee = new Employee();
        differentEmployee.setId(2L);
        differentEmployee.setEmail(differentEmail);
        differentEmployee.setFirstName("Jane");
        differentEmployee.setLastName("Smith");
        
        when(employeeService.findByEmailWithRoles(differentEmail)).thenReturn(differentEmployee);

        // Act
        Employee result = jwtService.getUser(tokenWithDifferentEmail);

        // Assert
        assertNotNull(result);
        assertEquals(differentEmail, result.getEmail());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        verify(employeeService).findByEmailWithRoles(differentEmail);
    }



    @Test
    @DisplayName("validateToken - Should handle empty token")
    void validateToken_ShouldHandleEmptyToken() {
        // Arrange
        String emptyToken = jwtService.generateJwtToken("a@example.com");

        // Act
        boolean result = jwtService.validateToken(emptyToken);

        // Assert
        assertFalse(result);

    }

    @Test
    @DisplayName("generateJwtToken - Should handle null email")
    void generateJwtToken_ShouldHandleNullEmail() {
        // Arrange
        String nullEmail = null;

        // Act
        String token = jwtService.generateJwtToken(nullEmail);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token structure
        Claims claims = Jwts.parserBuilder()
                .setSigningKey("testSecretKeyForJwtTokenGenerationAndValidation")
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        assertNull(claims.getSubject());
    }



    // Helper method to generate test tokens
    private String generateTestToken(String email, long expirationTime) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationTime))
                .signWith(SignatureAlgorithm.HS256, "testSecretKeyForJwtTokenGenerationAndValidation")
                .compact();
    }
} 