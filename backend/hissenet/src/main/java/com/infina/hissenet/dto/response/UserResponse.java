package com.infina.hissenet.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String position,
        Set<String> roleNames,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
