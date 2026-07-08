package com.keanusantos.personalfinancemanager.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class StandardError {

    private Instant timestamp;
    private HttpStatus status;
    private String error;
    private String message;
    private String path;

    public StandardError(){};

    public StandardError(Instant timestamp, HttpStatus status, String error, String message, String path) {
        this.timestamp = timestamp.truncatedTo(ChronoUnit.SECONDS);
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
