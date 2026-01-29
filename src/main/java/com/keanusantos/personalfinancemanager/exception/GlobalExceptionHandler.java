package com.keanusantos.personalfinancemanager.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    private ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        String errorMessage = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError errorBody = new StandardError(Instant.now(), status, errorMessage, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(errorBody);
    }

    @ExceptionHandler(BusinessException.class)
    private ResponseEntity<StandardError> businessExceptionHandler(BusinessException e, HttpServletRequest request) {
        String errorMessage = "BUSINESS EXCEPTION";
        HttpStatus status = e.getStatus();
        StandardError errorBody = new StandardError(Instant.now(), status, errorMessage, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(errorBody);
    }

}
