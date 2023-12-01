package com.example.chatapp.exception;

public class CustomRuntimeException extends RuntimeException {
    private ErrorType errorType;

    public CustomRuntimeException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }
}
