package com.infina.hissenet.validation;

/**
 * Enumeration defining the types of unique values that can be validated.
 * Each type corresponds to a specific entity and field combination that requires
 * uniqueness validation.
 *
 * <p>This enum is used by the {@link UniqueValue} annotation to determine which
 * repository and validation method should be used for checking uniqueness.</p>
 *
 * <p>Each enum constant contains a message key that is used to retrieve localized
 * error messages when validation fails.</p>
 *
 * @author Furkan Can
 * @version 1.0
 * @since 1.0
 * @see UniqueValue
 * @see UniqueValueValidator
 */
public enum UniqueValueType {
    
    /**
     * Validates that an employee email address is unique across all employees.
     * Uses {@link com.infina.hissenet.repository.EmployeeRepository#existsByEmail(String)}.
     */
    EMPLOYEE_EMAIL("customer.email.already.exists"),
    
    /**
     * Validates that a customer email address is unique across all customers.
     * Uses {@link com.infina.hissenet.service.CustomerService#existsByEmail(String)}.
     */
    CUSTOMER_EMAIL("customer.email.already.exists"),
    
    /**
     * Validates that a tax number is unique across all corporate customers.
     * Uses {@link com.infina.hissenet.repository.CorporateCustomerRepository#existsByTaxNumber(String)}.
     */
    TAX_NUMBER("customer.tax.number.already.exists"),
    
    /**
     * Validates that a TC (Turkish Citizenship) number is unique across all individual customers.
     * Uses {@link com.infina.hissenet.repository.IndividualCustomerRepository#existsByTcNumber(String)}.
     */
    TC_NUMBER("customer.tc.number.already.exists");

    /**
     * The message key used to retrieve localized error messages.
     */
    private final String messageKey;

    /**
     * Constructs a new UniqueValueType with the specified message key.
     *
     * @param messageKey the message key for localized error messages
     */
    UniqueValueType(String messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * Gets the message key associated with this unique value type.
     * This key is used to retrieve localized error messages when validation fails.
     *
     * @return the message key for this unique value type
     */
    public String getMessageKey() {
        return messageKey;
    }
}