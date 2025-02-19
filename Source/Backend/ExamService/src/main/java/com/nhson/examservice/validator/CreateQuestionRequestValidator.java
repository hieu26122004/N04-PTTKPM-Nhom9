package com.nhson.examservice.validator;

import com.nhson.examservice.question.entities.Option;
import com.nhson.examservice.question.entities.Question;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateQuestionRequestValidator implements ConstraintValidator<ValidCreateQuestionRequest, List<Question>> {

    @Override
    public boolean isValid(List<Question> questions, ConstraintValidatorContext context) {
        if (CollectionUtils.isEmpty(questions)) {
            buildViolation(context, "Questions must not be null or empty.");
            return false;
        }
        String examId = questions.get(0).getExamId();
        if (examId == null || examId.trim().isEmpty()) {
            buildViolation(context, "ExamId must not be null or empty.");
            return false;
        }
        for (Question question : questions) {
            if (question == null) {
                buildViolation(context, "Question must not be null.");
                return false;
            }
            if (question.getContent() == null || question.getContent().trim().isEmpty()) {
                buildViolation(context, "Question content must not be null or empty.");
                return false;
            }
            if (!examId.equals(question.getExamId())) {
                buildViolation(context, "All questions must have the same examId.");
                return false;
            }
            List<Option> options = question.getOptions();
            if (CollectionUtils.isEmpty(options)) {
                buildViolation(context, "Question options must not be null or empty.");
                return false;
            }
            if (!validateOptions(options, context)) {
                return false;
            }
        }

        return true;
    }

    private boolean validateOptions(List<Option> options, ConstraintValidatorContext context) {
        boolean hasCorrectOption = false;
        Set<String> labels = new HashSet<>();

        for (Option option : options) {
            if (option == null) {
                buildViolation(context, "Option must not be null.");
                return false;
            }
            if (option.getContent() == null || option.getContent().trim().isEmpty()) {
                buildViolation(context, "Option content must not be null or empty.");
                return false;
            }
            if (option.getLabel() == null || option.getLabel().trim().isEmpty()) {
                buildViolation(context, "Option label must not be null or empty.");
                return false;
            }
            if (!labels.add(option.getLabel())) {
                buildViolation(context, "Option labels must be unique.");
                return false;
            }

            if (option.getIsCorrect() != null && option.getIsCorrect()) {
                if (hasCorrectOption) {
                    buildViolation(context, "Only one option can be marked as correct.");
                    return false;
                }
                hasCorrectOption = true;
            }
        }
        if (!hasCorrectOption) {
            buildViolation(context, "At least one option must be marked as correct.");
            return false;
        }

        return true;
    }

    private void buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
