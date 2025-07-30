package com.infina.hissenet.dto.request;

public record AccountCreateRequest(
		String username,
		String passwordHash,
		Long employeeId
) {

}
