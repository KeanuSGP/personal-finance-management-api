package com.keanusantos.personalfinancemanager.domain.user.exception;

public class NameAlreadyExistsException extends RuntimeException {
    public NameAlreadyExistsException() {
        super("Name already in use");
    }
}
