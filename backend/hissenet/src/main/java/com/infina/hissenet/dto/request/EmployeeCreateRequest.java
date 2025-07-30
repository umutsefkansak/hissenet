package com.infina.hissenet.dto.request;

import java.util.Set;

public record EmployeeCreateRequest(
		String firstName,
		String lastName,
		String email,
		String phone,
		String position,
		String emergencyContactName,
		Set<Long> roleIds
) {

}
