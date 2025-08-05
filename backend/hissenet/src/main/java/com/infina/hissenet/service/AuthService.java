package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.LoginRequest;
import com.infina.hissenet.dto.response.AuthResponse;
import com.infina.hissenet.entity.Employee;
import com.infina.hissenet.exception.auth.LoginException;
import com.infina.hissenet.mapper.EmployeeMapper;
import com.infina.hissenet.security.JwtService;
import com.infina.hissenet.security.RedisTokenService;
import com.infina.hissenet.service.abstracts.IAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService implements IAuthService {
    private final EmployeeService employeeService;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final EmployeeMapper mapper;
    private final RedisTokenService redisTokenService;

    public AuthService(EmployeeService employeeService, JwtService jwtService, PasswordEncoder encoder, EmployeeMapper mapper, RedisTokenService redisTokenService) {
        this.employeeService = employeeService;
        this.jwtService = jwtService;
        this.encoder = encoder;
        this.mapper = mapper;
        this.redisTokenService = redisTokenService;
    }

    // login
    public AuthResponse login(LoginRequest request) {
        Employee employee = employeeService.findByEmail(request.email());
        if (!encoder.matches(request.password(), employee.getPassword())) {
            throw new LoginException();
        }

        String token = jwtService.generateJwtToken(request.email());
        String sessionId= UUID.randomUUID().toString();
        int oneWeek = 7 * 24 * 60 * 60;
        redisTokenService.saveSession(sessionId,token,oneWeek);

        return new AuthResponse(mapper.toUserResponse(employee),sessionId,oneWeek);
    }

    // logout
    public String logout(String sessionId) {
        redisTokenService.deleteSession(sessionId);
        return "You have been logged out";
    }



}
