package com.hivetech.kanban.exception;

import java.time.Instant;
import java.util.List;

public class ApiError {

    private int status;
    private String message;
    private Instant timestamp;
    private List<String> errors;

    public ApiError(int status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = Instant.now();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public List<String> getErrors() {
        return errors;
    }
}