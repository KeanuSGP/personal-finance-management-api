package com.keanusantos.personalfinancemanager.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(Long id) {
        super("Resource not found. id " + id, HttpStatus.NOT_FOUND);
    }

}
