package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.Size;

public record RoleUpdateRequest(
        @Size(max = 50, message = "{validation.role.name.size}")
        String name,

        @Size(max = 255, message = "{validation.role.description.size}")
        String description,

        Boolean isActive
) {}