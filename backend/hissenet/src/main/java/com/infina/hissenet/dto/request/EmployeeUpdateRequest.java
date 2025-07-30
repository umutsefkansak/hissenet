package com.infina.hissenet.dto.request;

import java.util.Set;

public record EmployeeUpdateRequest(
		String firstName,
		String lastName,
		String email,
		String phone,
		String position,
		String emergencyContactName,
		String emergencyContactPhone,
		Set<Long> roleIds
) {

}
