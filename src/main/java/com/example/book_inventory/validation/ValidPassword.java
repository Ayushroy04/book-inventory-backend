package com.example.book_inventory.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface ValidPassword {

    String message() default "Password must be at least 8 characters and contain uppercase, lowercase, digit, and special character (@$!%*?&)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
