package com.nhson.authservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;

public class RegisterRequestValidator implements ConstraintValidator<ValidRegisterRequest, Map<String, String>> {

    @Override
    public boolean isValid(Map<String, String> requestBody, ConstraintValidatorContext context) {
        if (requestBody == null) {
            context.buildConstraintViolationWithTemplate("Request body cannot be null")
                    .addConstraintViolation();
            return false;
        }
        context.disableDefaultConstraintViolation();
        boolean isValid = true;

        String username = requestBody.get("username");
        String password = requestBody.get("password");
        String email = requestBody.get("email");

        // Validate username
        if (username == null || username.isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Username is required")
                    .addConstraintViolation();
            isValid = false;
        } else if (username.length() < 5 || username.length() > 20) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Username must be between 5 and 20 characters long")
                    .addConstraintViolation();
            isValid = false;
        } else if (username.contains(" ")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Username must not contain spaces")
                    .addConstraintViolation();
            isValid = false;
        }

        if (password == null || password.isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password is required")
                    .addConstraintViolation();
            isValid = false;
        } else if (password.length() < 6 || password.length() > 20) {
            context.buildConstraintViolationWithTemplate("Password must be between 6 and 20 characters long")
                    .addConstraintViolation();
            isValid = false;
        } else if (password.contains(" ")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must not contain spaces")
                    .addConstraintViolation();
            isValid = false;
        }

        // Validate email
        if (email == null || email.isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Email is required")
                    .addConstraintViolation();
            isValid = false;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Email is not valid")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}

