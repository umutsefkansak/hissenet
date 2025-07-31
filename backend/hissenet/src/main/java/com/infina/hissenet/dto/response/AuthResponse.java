package com.infina.hissenet.dto.response;

public record AuthResponse(
       EmployeeResponse employee,
       String token
) {
}
