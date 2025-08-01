package com.infina.hissenet.security;

import com.infina.hissenet.entity.Employee;
import com.infina.hissenet.security.abstracts.IJwtService;
import com.infina.hissenet.service.EmployeeService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService implements IJwtService {
    private final EmployeeService employeeService;
    @Value("${jwt.secret}")
    private String JWT_SECRET;
    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;


    public JwtService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // token oluştur
    public String generateJwtToken(String email) {
        return Jwts.builder()
                .setSubject(email).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }

    // geçerlilik süresi
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // claimsin içini al
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(JWT_SECRET).build().parseClaimsJws(token).getBody();
    }

    // useri al
    public Employee getUser(String token) {
        String email = getEmail(token);
        return employeeService.findByEmailWithRoles(email);
    }

    // validate token
    public boolean validateToken(String token) {
        Employee user = getUser(token);
        return user != null && !isTokenExpired(token);
    }

    //getmail
    private String getEmail(String token) {
        return extractClaims(token).getSubject();
    }

}

