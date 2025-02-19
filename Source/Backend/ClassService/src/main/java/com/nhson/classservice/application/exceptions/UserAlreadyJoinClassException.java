package com.nhson.classservice.application.exceptions;

public class UserAlreadyJoinClassException extends RuntimeException{
    public UserAlreadyJoinClassException(String message) {
        super(message);
    }
}
