package com.keanusantos.personalfinancemanager.domain.user.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String error) {
        super(error);
    }
}
