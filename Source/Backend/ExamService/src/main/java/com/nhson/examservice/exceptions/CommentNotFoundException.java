package com.nhson.examservice.exceptions;

public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException(String message) {
        super(message);
    }
}
