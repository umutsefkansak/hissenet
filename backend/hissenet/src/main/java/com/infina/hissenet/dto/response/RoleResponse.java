package com.infina.hissenet.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record RoleResponse(
        Long id,
        String name,
        String description,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Set<EmployeeResponse> employees
) {}