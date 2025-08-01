package com.infina.hissenet.dto.response;

public record AuthResponse(
       EmployeeResponse employee,
       String sessionId,
       long time
) {
}
