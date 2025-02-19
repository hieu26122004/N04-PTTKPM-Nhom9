package com.nhson.autograderservice;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, String errorCode, LocalDateTime timestamp) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

