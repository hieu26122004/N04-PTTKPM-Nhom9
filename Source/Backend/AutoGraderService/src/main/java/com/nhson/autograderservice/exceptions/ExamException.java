package com.nhson.autograderservice.exceptions;

public class ExamException extends RuntimeException {
    private String errorCode;

    public ExamException(String message) {
        super(message);
    }

    public ExamException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

