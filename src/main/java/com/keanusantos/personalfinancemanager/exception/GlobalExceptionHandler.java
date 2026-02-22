package com.keanusantos.personalfinancemanager.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    private ResponseEntity<StandardError> businessExceptionHandler(BusinessException e, HttpServletRequest request) {
        String errorMessage = "BUSINESS_EXCEPTION";
        HttpStatus status = e.getStatus();
        StandardError errorBody = new StandardError(Instant.now(), status, errorMessage, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(errorBody);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<StandardError> ConstraintViolationHandler(ConstraintViolationException e, HttpServletRequest request) {
        String errorMessage = null;
        String field = null;
        HttpStatus status = HttpStatus.BAD_REQUEST;

        for (ConstraintViolation<?> v: e.getConstraintViolations()) {
            field = "The " + v.getPropertyPath().toString() + " field is empty";
            errorMessage = v.getMessage();
        }

        StandardError errorBody = new StandardError(Instant.now(), status, errorMessage, field, request.getRequestURI());
        return ResponseEntity.status(status).body(errorBody);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        String error = "Argument not valid";

        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        HttpStatus status = HttpStatus.valueOf(statusCode.value());


        StandardError errorBody = new StandardError(Instant.now(), status, error, message, request.getDescription(false));
        return ResponseEntity.status(status).body(errorBody);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        String error = "Argument Not Valid";
        HttpStatus status = HttpStatus.valueOf(statusCode.value());
        String message = "There is an error in the data sent";

        if (e.getCause() instanceof InvalidFormatException cause) {
            String path = cause.getValue().toString() + " is not a valid value";
            StandardError errorBody = new StandardError(Instant.now(), status, error, path, request.getDescription(false));
            return ResponseEntity.status(status).body(errorBody);
        }

        StandardError errorBody = new StandardError(Instant.now(), status, error, message, request.getDescription(false));
        return ResponseEntity.status(status).body(errorBody);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<StandardError> illegalArgumentHandler(IllegalArgumentException e, HttpServletRequest request) {
        String errorMessage = e.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError errorBody = new StandardError(Instant.now(), status, errorMessage, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(errorBody);
    }

}
