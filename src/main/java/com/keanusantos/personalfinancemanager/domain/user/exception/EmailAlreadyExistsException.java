package com.keanusantos.personalfinancemanager.domain.user.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException() {
        super("Email already in use");
    }
}
