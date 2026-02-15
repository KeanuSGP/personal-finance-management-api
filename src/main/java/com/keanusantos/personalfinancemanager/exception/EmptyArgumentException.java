package com.keanusantos.personalfinancemanager.exception;

import org.springframework.http.HttpStatus;

public class EmptyArgumentException extends BusinessException {
    public EmptyArgumentException(String field) {
        super("The " + field + " cannot be empty", HttpStatus.BAD_REQUEST);
    }
}
