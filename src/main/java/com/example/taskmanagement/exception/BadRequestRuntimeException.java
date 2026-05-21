package com.example.taskmanagement.exception;

import java.util.List;

public class BadRequestRuntimeException
        extends RuntimeException {

    private List<String> errors;

    // Single error constructor
    public BadRequestRuntimeException(
            String message) {

        super(message);
    }

    // Multiple errors constructor
    public BadRequestRuntimeException(
            List<String> errors) {

        super("Validation failed");

        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
