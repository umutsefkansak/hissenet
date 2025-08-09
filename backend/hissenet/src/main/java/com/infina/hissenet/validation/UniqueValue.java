package com.infina.hissenet.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for ensuring unique values across different entity types.
 * This annotation is used to validate that a given value (email, tax number, TC number, etc.)
 * does not already exist in the system for the specified entity type.
 *
 * <p>The annotation supports multiple entity types through the {@link UniqueValueType} enum
 * and allows for custom error messages to be provided.</p>
 *
 * <p>Usage examples:</p>
 * <pre>
 * {@code
 * // Validate unique customer email
 * @UniqueValue(type = UniqueValueType.CUSTOMER_EMAIL)
 * private String email;
 *
 * // Validate unique tax number with custom message
 * @UniqueValue(type = UniqueValueType.TAX_NUMBER, customMessage = "Tax number already exists")
 * private String taxNumber;
 * }
 * </pre>
 *
 * <p>This annotation is processed by {@link UniqueValueValidator} which performs the actual
 * validation logic by checking the appropriate repository for existing values.</p>
 *
 * @author Furkan Can
 * @version 1.0
 * @since 1.0
 * @see UniqueValueValidator
 * @see UniqueValueType
 */
@Constraint(validatedBy = UniqueValueValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueValue {

    String message() default "This value is already registered in the system";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    UniqueValueType type();

    String customMessage() default "";
}
