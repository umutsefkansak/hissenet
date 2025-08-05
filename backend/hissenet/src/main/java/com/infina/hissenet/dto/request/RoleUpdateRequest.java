package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.Size;

public record RoleUpdateRequest(
        @Size(max = 50, message = "Role name cannot exceed 50 characters")
        String name,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description,

        Boolean isActive
) {}