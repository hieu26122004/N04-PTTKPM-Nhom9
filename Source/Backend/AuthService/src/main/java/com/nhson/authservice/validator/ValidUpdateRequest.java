package com.nhson.authservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UpdateRequestValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUpdateRequest {
    String message() default "Invalid update request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
