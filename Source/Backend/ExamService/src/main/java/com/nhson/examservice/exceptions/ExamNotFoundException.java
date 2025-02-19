package com.nhson.examservice.exceptions;

public class ExamNotFoundException extends RuntimeException {
    public ExamNotFoundException(String message) {
        super(message);
    }
}
