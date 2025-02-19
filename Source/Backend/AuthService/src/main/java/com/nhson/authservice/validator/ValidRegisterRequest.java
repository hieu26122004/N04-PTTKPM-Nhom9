package com.nhson.authservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RegisterRequestValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRegisterRequest {
    String message() default "Invalid register request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
