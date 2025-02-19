package com.nhson.classservice.application.exceptions;

public class PermissionDeniedException extends RuntimeException{
    public PermissionDeniedException(String message) {
        super(message);
    }
}
