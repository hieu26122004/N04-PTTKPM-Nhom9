package com.nhson.examservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CreateQuestionRequestValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCreateQuestionRequest {
    String message() default "Invalid questions create request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
