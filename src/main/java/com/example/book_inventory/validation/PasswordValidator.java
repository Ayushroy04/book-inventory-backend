package com.example.book_inventory.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    // Password must contain:
    // - At least 8 characters
    // - At least one uppercase letter
    // - At least one lowercase letter
    // - At least one digit
    // - At least one special character (@$!%*?&)
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        boolean isValid = pattern.matcher(password).matches();

        if (!isValid) {
            // Provide detailed error message
            context.disableDefaultConstraintViolation();

            if (password.length() < 8) {
                context.buildConstraintViolationWithTemplate(
                        "Password must be at least 8 characters long").addConstraintViolation();
            } else if (!password.matches(".*[A-Z].*")) {
                context.buildConstraintViolationWithTemplate(
                        "Password must contain at least one uppercase letter").addConstraintViolation();
            } else if (!password.matches(".*[a-z].*")) {
                context.buildConstraintViolationWithTemplate(
                        "Password must contain at least one lowercase letter").addConstraintViolation();
            } else if (!password.matches(".*\\d.*")) {
                context.buildConstraintViolationWithTemplate(
                        "Password must contain at least one digit").addConstraintViolation();
            } else if (!password.matches(".*[@$!%*?&].*")) {
                context.buildConstraintViolationWithTemplate(
                        "Password must contain at least one special character (@$!%*?&)").addConstraintViolation();
            }
        }

        return isValid;
    }
}
