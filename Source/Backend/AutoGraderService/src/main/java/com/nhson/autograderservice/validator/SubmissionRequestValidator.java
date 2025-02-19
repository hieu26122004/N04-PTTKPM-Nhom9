package com.nhson.autograderservice.validator;

import com.nhson.autograderservice.grader.model.Attempt;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SubmissionRequestValidator implements ConstraintValidator<ValidSubmissionRequest, Attempt> {
    @Override
    public boolean isValid(Attempt attempt, ConstraintValidatorContext context) {
        boolean isValid = true;

        if(attempt.getAttemptId() != null && !attempt.getAttemptId().trim().isEmpty()) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Attempt id should be empty, then server will assign attempt id.").addPropertyNode("attemptId").addConstraintViolation();
        }
        if(attempt.getResult() != null) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Result should be empty, then server will calculate result.").addPropertyNode("result").addConstraintViolation();
        }
        if(attempt.getUserAnswer() == null){
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("User answers should not be empty.").addPropertyNode("userAnswer").addConstraintViolation();
        }
        if(attempt.getExamId() == null || attempt.getExamId().trim().isEmpty()) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Exam id should not be empty.").addPropertyNode("examId").addConstraintViolation();
        }
        if(attempt.getTotalTime() == null || attempt.getTotalTime() < 0) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Total time should not be negative.").addPropertyNode("totalTime").addConstraintViolation();
        }
        return isValid;
    }
}
