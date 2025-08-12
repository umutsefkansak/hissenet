package com.infina.hissenet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleCreateRequest(
        @NotBlank(message = "{validation.role.name.required}")
        @Size(max = 50, message = "{validation.role.name.size}")
        String name,

        @Size(max = 255, message = "{validation.role.description.size}")
        String description,

        Boolean isActive
) {}