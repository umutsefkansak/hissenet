package com.infina.hissenet.exception.employee;

import com.infina.hissenet.exception.common.NotFoundException;

public class EmployeeNotFoundException extends NotFoundException {
	public EmployeeNotFoundException(Long id) {
        super("Employee not found with id: " + id);
    }

    public EmployeeNotFoundException() {
        super("Employee not found");
    }
}
