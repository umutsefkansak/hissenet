package com.infina.hissenet.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;


public record AuthResponse(
       UserResponse response,
       @JsonIgnore
       String sessionId,
       @JsonIgnore
       long time

) {
}
