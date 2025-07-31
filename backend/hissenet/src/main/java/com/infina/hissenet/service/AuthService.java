package com.infina.hissenet.service;

import com.infina.hissenet.dto.request.LoginRequest;
import com.infina.hissenet.dto.response.AuthResponse;
import com.infina.hissenet.entity.Employee;
import com.infina.hissenet.exception.LoginException;
import com.infina.hissenet.mapper.EmployeeMapper;
import com.infina.hissenet.security.JwtService;
import com.infina.hissenet.service.abstracts.IAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {
    private final EmployeeService employeeService;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final EmployeeMapper mapper;

    public AuthService(EmployeeService employeeService, JwtService jwtService, PasswordEncoder encoder, EmployeeMapper mapper) {
        this.employeeService = employeeService;
        this.jwtService = jwtService;
        this.encoder = encoder;
        this.mapper = mapper;
    }

    // login
    public AuthResponse login(LoginRequest request) {
        Employee employee = employeeService.findByEmail(request.email());
        if (!encoder.matches(request.password(), employee.getPassword())) {
            throw new LoginException();
        }
        String token = jwtService.generateJwtToken(request.email());
        return new AuthResponse(mapper.toResponse(employee), token);
    }

    // logout
    public String logout() {
        return "You have been logged out";
    }


}
