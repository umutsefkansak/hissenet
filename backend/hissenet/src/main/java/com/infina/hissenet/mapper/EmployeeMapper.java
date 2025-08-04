package com.infina.hissenet.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.infina.hissenet.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.infina.hissenet.dto.request.EmployeeCreateRequest;
import com.infina.hissenet.dto.request.EmployeeUpdateRequest;
import com.infina.hissenet.dto.response.EmployeeResponse;
import com.infina.hissenet.entity.Employee;
import com.infina.hissenet.entity.Role;


@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "hireDate", ignore = true)
    @Mapping(target = "terminationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    Employee toEntity(EmployeeCreateRequest dto);

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "hireDate", ignore = true)
    @Mapping(target = "terminationDate", ignore = true)
    Employee toEntity(EmployeeUpdateRequest dto);

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "roles", target = "roleIds", qualifiedByName = "mapToId")
    EmployeeResponse toResponse(Employee entity);

    @Mapping(source = "roles", target = "roleNames", qualifiedByName = "mapToName")
    UserResponse toUserResponse(Employee entity);

    @Named("mapToId")
    default Set<Long> map(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return java.util.Set.of();
        }
        return roles.stream().map(Role::getId).collect(Collectors.toSet());
    }

    @Named("mapToName")
    default Set<String> mapToName(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return java.util.Set.of();
        }
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }
}