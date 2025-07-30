package com.infina.hissenet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.infina.hissenet.dto.request.AccountCreateRequest;
import com.infina.hissenet.dto.response.AccountResponse;
import com.infina.hissenet.entity.Account;

@Mapper(
		componentModel = "spring",
		unmappedSourcePolicy = ReportingPolicy.IGNORE,
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AccountMapper {
	AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);
	
	@Mapping(target = "employee", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "lastLoginIp", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "accountLockedUntil", ignore = true)
    @Mapping(target = "passwordChangedAt", ignore = true)
    @Mapping(target = "twoFactorEnabled", ignore = true)
    @Mapping(target = "mustChangePassword", ignore = true)
    Account toEntity(AccountCreateRequest dto);
	
	@Mapping(source = "employee.id", target = "employeeId")
    AccountResponse toResponse(Account entity);
	
}
