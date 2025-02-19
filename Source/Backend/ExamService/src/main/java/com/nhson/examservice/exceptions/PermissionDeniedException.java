package com.nhson.examservice.exceptions;

public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException(String message) {
        super(message);
    }
}

