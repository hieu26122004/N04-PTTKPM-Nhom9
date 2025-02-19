package com.nhson.authservice.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LoginRequestValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLoginRequest {
    String message() default "Invalid login request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
