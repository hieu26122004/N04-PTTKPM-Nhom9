package com.nhson.examservice.exceptions;

public class  QuestionCountMismatchException extends RuntimeException {
    public QuestionCountMismatchException(String message) {
        super(message);
    }
}
