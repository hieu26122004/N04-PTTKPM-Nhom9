package com.nhson.authservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;

public class UpdateRequestValidator implements ConstraintValidator<ValidUpdateRequest, Map<String,?>> {
    @Override
    public boolean isValid(Map<String, ?> requestBody, ConstraintValidatorContext context) {
        if(requestBody == null){
            return false;
        }

        context.disableDefaultConstraintViolation();

        if(!requestBody.containsKey("userId")){
            context.buildConstraintViolationWithTemplate("userId is required for update operations")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
