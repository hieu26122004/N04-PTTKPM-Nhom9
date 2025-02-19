package com.nhson.autograderservice.exceptions;

public class PermissionDeniedException extends RuntimeException{
    public PermissionDeniedException(String message) {
        super(message);
    }
}
