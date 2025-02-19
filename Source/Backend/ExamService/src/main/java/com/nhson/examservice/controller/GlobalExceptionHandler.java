package com.nhson.examservice.controller;

import com.nhson.examservice.exceptions.CommentNotFoundException;
import com.nhson.examservice.exceptions.ExamNotFoundException;
import com.nhson.examservice.exceptions.PermissionDeniedException;
import com.nhson.examservice.exceptions.QuestionCountMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<String> handlePermissionDeniedException(PermissionDeniedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
    @ExceptionHandler(InvalidBearerTokenException.class)
    public ResponseEntity<String> handleInvalidBearerTokenException(InvalidBearerTokenException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
    @ExceptionHandler(ExamNotFoundException.class)
    public ResponseEntity<String> handleExamNotFoundException(ExamNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(QuestionCountMismatchException.class)
    public ResponseEntity<String> handleQuestionCountMismatchException(QuestionCountMismatchException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFoundException(CommentNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
