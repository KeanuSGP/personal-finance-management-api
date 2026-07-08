package com.keanusantos.personalfinancemanager.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException() {
        super("Resource not found", HttpStatus.NOT_FOUND);
    }

}
