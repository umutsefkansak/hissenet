package com.infina.hissenet.security.abstracts;

import com.infina.hissenet.entity.Employee;

public interface IJwtService {
    String generateJwtToken(String email);
    Employee getUser(String token);
    boolean validateToken(String token);
}
