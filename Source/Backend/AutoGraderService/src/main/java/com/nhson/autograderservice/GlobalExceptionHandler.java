package com.nhson.autograderservice;

import com.nhson.autograderservice.exceptions.ExamException;
import com.nhson.autograderservice.exceptions.PermissionDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ExamException.class)
    public ResponseEntity<ErrorResponse> handleExamException(ExamException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                ex.getErrorCode(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<String> handlePermissionDeniedException(PermissionDeniedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

}
