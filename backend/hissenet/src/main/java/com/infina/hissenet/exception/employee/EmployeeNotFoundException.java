package com.infina.hissenet.exception.employee;

import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.utils.MessageUtils;

public class EmployeeNotFoundException extends NotFoundException {
    public EmployeeNotFoundException(Long id) {
        super(MessageUtils.getMessage("employee.not.found", id));
    }

    public EmployeeNotFoundException() {
        super(MessageUtils.getMessage("employee.not.found", ""));
    }
}