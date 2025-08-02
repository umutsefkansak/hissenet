package com.infina.hissenet.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;


public record AuthResponse(
       EmployeeResponse employee,
       @JsonIgnore
       String sessionId,
       @JsonIgnore
       long time

) {
}
