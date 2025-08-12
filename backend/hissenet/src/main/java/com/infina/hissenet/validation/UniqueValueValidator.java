package com.infina.hissenet.validation;

import com.infina.hissenet.repository.EmployeeRepository;
import com.infina.hissenet.service.CustomerService;
import com.infina.hissenet.repository.CorporateCustomerRepository;
import com.infina.hissenet.repository.IndividualCustomerRepository;
import com.infina.hissenet.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

/**
 * Validator implementation for the {@link UniqueValue} annotation.
 * This class performs the actual validation logic to ensure that values are unique
 * across different entity types in the system.
 *
 * <p>The validator supports multiple entity types through the {@link UniqueValueType} enum
 * and delegates the actual uniqueness check to the appropriate repository or service.</p>
 *
 * <p>Validation logic:</p>
 * <ul>
 *   <li>Null or empty values are considered valid (use @NotNull or @NotBlank for required validation)</li>
 *   <li>For non-empty values, checks the appropriate repository for existing records</li>
 *   <li>If a duplicate is found, builds a constraint violation with the appropriate error message</li>
 *   <li>Supports custom error messages through the annotation's customMessage attribute</li>
 * </ul>
 *
 * <p>Supported validation types:</p>
 * <ul>
 *   <li>{@link UniqueValueType#EMPLOYEE_EMAIL} - Validates unique employee emails</li>
 *   <li>{@link UniqueValueType#CUSTOMER_EMAIL} - Validates unique customer emails</li>
 *   <li>{@link UniqueValueType#TAX_NUMBER} - Validates unique corporate customer tax numbers</li>
 *   <li>{@link UniqueValueType#TC_NUMBER} - Validates unique individual customer TC numbers</li>
 * </ul>
 *
 * <p>This validator is automatically registered as a Spring component and can be used
 * with the {@link UniqueValue} annotation on entity fields.</p>
 *
 * @author Furkan Can
 * @version 1.0
 * @since 1.0
 * @see UniqueValue
 * @see UniqueValueType
 */
@Component
public class UniqueValueValidator implements ConstraintValidator<UniqueValue, String> {

    /**
     * Service for customer-related operations.
     */
    private final CustomerService customerService;
    
    /**
     * Repository for corporate customer operations.
     */
    private final CorporateCustomerRepository corporateRepository;
    
    /**
     * Repository for individual customer operations.
     */
    private final IndividualCustomerRepository individualRepository;
    
    /**
     * Repository for employee operations.
     */
    private final EmployeeRepository employeeRepository;

    /**
     * The type of unique value to validate.
     */
    private UniqueValueType type;
    
    /**
     * Custom error message to use if provided.
     */
    private String customMessage;

    /**
     * Constructs a new UniqueValueValidator with the required dependencies.
     *
     * @param customerService service for customer operations
     * @param corporateRepository repository for corporate customer operations
     * @param individualRepository repository for individual customer operations
     * @param employeeRepository repository for employee operations
     */
    public UniqueValueValidator(CustomerService customerService,
                                CorporateCustomerRepository corporateRepository,
                                IndividualCustomerRepository individualRepository,
                                EmployeeRepository employeeRepository) {
        this.customerService = customerService;
        this.corporateRepository = corporateRepository;
        this.individualRepository = individualRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Initializes the validator with the constraint annotation.
     * This method is called once when the validator is instantiated.
     *
     * @param constraintAnnotation the constraint annotation
     */
    @Override
    public void initialize(UniqueValue constraintAnnotation) {
        this.type = constraintAnnotation.type();
        this.customMessage = constraintAnnotation.customMessage();
    }

    /**
     * Performs the validation logic.
     * 
     * <p>Validation steps:</p>
     * <ol>
     *   <li>If the value is null or empty, return true (valid)</li>
     *   <li>Based on the type, check the appropriate repository for existing values</li>
     *   <li>If a duplicate is found, build a constraint violation and return false</li>
     *   <li>Otherwise, return true (valid)</li>
     * </ol>
     *
     * @param value the value to validate
     * @param context the constraint validator context
     * @return true if the value is unique, false otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null or empty values are considered valid (use @NotNull or @NotBlank for required validation)
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        // Check for existing values based on the type
        boolean exists = switch (type) {
            case CUSTOMER_EMAIL -> customerService.existsByEmail(value);
            case TAX_NUMBER -> corporateRepository.existsByTaxNumber(value);
            case TC_NUMBER -> individualRepository.existsByTcNumber(value);
            case EMPLOYEE_EMAIL -> employeeRepository.existsByEmail(value);
        };

        // If a duplicate is found, build the constraint violation
        if (exists) {
            context.disableDefaultConstraintViolation();

            String messageToUse;
            if (!customMessage.isEmpty()) {
                messageToUse = customMessage;
            } else {
                messageToUse = MessageUtils.getMessage(type.getMessageKey(), value);
            }

            context.buildConstraintViolationWithTemplate(messageToUse)
                    .addConstraintViolation();
        }

        return !exists;
    }
}