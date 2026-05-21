package com.example.taskmanagement.exception;

public class BadRequestRuntimeException
        extends RuntimeException {

    public BadRequestRuntimeException(
            String message) {

        super(message);
    }

    public BadRequestRuntimeException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}