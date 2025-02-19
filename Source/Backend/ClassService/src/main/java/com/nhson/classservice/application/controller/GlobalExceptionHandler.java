package com.nhson.classservice.application.controller;

import com.nhson.classservice.application.exceptions.ClassNotFoundException;
import com.nhson.classservice.application.exceptions.PermissionDeniedException;
import com.nhson.classservice.application.exceptions.UserAlreadyJoinClassException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyJoinClassException.class)
    public ResponseEntity<String> handleUserAlreadyJoinClassException(UserAlreadyJoinClassException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<String> handlePermissionDeniedException(PermissionDeniedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
    @ExceptionHandler(ClassNotFoundException.class)
    public ResponseEntity<String> handleClassNotFoundException(ClassNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
