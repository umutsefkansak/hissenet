package com.infina.hissenet.dto.response;

import java.time.LocalDateTime;

import com.infina.hissenet.entity.enums.AccountStatus;

public record AccountResponse(
		Long id,
        String username,
        AccountStatus status,
        LocalDateTime lastLoginAt,
        Long employeeId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
