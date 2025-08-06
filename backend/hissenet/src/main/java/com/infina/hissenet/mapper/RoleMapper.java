package com.infina.hissenet.mapper;

import com.infina.hissenet.dto.request.RoleCreateRequest;
import com.infina.hissenet.dto.request.RoleUpdateRequest;
import com.infina.hissenet.dto.response.RoleResponse;
import com.infina.hissenet.dto.response.EmployeeResponse;
import com.infina.hissenet.entity.Employee;
import com.infina.hissenet.entity.Role;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "active", source = "isActive", defaultValue = "true")
    Role toEntity(RoleCreateRequest createRoleDto);

    @Mapping(target = "employees", source = "employees", qualifiedByName = "mapEmployeesToResponse")
    RoleResponse toDto(Role role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateEntityFromDto(RoleUpdateRequest updateRoleDto, @MappingTarget Role role);

    @Named("mapEmployeesToResponse")
    default Set<EmployeeResponse> mapEmployeesToResponse(Set<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            return Set.of();
        }

        return employees.stream()
                .map(employee -> {
                    // Employee'nin sahip olduğu role ID'lerini topla
                    Set<Long> roleIds = employee.getRoles() != null ?
                            employee.getRoles().stream()
                                    .map(Role::getId)
                                    .collect(Collectors.toSet()) : Set.of();

                    return new EmployeeResponse(
                            employee.getId(),
                            employee.getFirstName(),
                            employee.getLastName(),
                            employee.getEmail(),
                            employee.getPhone(),
                            employee.getPosition(),
                            employee.getHireDate(),
                            employee.getTerminationDate(),
                            employee.getStatus(),
                            employee.getEmergencyContactName(),
                            employee.getEmergencyContactPhone(),
                            employee.getAccount() != null ? employee.getAccount().getId() : null,
                            roleIds,
                            employee.getCreatedAt(),
                            employee.getUpdatedAt(),
                            employee.getIsOnLeave()
                    );
                })
                .collect(Collectors.toSet());
    }
}