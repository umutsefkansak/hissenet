package com.infina.hissenet.exception;

public class EmployeeNotFoundException extends NotFoundException{
	public EmployeeNotFoundException(Long id) {
        super("Employee not found with id: " + id);
    }
}
