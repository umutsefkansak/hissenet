package com.infina.hissenet.validation;

import com.infina.hissenet.repository.EmployeeRepository;
import com.infina.hissenet.service.CustomerService;
import com.infina.hissenet.repository.CorporateCustomerRepository;
import com.infina.hissenet.repository.IndividualCustomerRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueValueValidator implements ConstraintValidator<UniqueValue, String> {

    private final CustomerService customerService;
    private final CorporateCustomerRepository corporateRepository;
    private final IndividualCustomerRepository individualRepository;
    private final EmployeeRepository employeeRepository;

    private UniqueValueType type;
    private String customMessage;

    public UniqueValueValidator(CustomerService customerService,
                                CorporateCustomerRepository corporateRepository,
                                IndividualCustomerRepository individualRepository,
                                EmployeeRepository employeeRepository) {
        this.customerService = customerService;
        this.corporateRepository = corporateRepository;
        this.individualRepository = individualRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void initialize(UniqueValue constraintAnnotation) {
        this.type = constraintAnnotation.type();
        this.customMessage = constraintAnnotation.customMessage();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        boolean exists = switch (type) {
            case CUSTOMER_EMAIL -> customerService.existsByEmail(value);
            case TAX_NUMBER -> corporateRepository.existsByTaxNumber(value);
            case TC_NUMBER -> individualRepository.existsByTcNumber(value);
            case EMPLOYEE_EMAIL -> employeeRepository.existsByEmail(value);
        };

        if (exists) {
            // Disable default message
            context.disableDefaultConstraintViolation();

            // Use custom message if provided, otherwise use enum's default message
            String messageToUse = !customMessage.isEmpty() ? customMessage : type.getDefaultMessage();

            context.buildConstraintViolationWithTemplate(messageToUse)
                    .addConstraintViolation();
        }

        return !exists;
    }
}