package com.example.chatapp.exception;

public enum ErrorType {
    DUPLICATE_USER_ERROR("User already exists"),
    DUPLICATE_USER_MESSAGE_ERROR("Messaging to self is not allowed"),
    USER_NOT_FOUND("User not found"),
    AUTHENTICATION_ERROR("Unauthorized access"),
    GROUP_NOT_FOUND("Group not found"),
    DUPLICATE_GROUP_NAME("Group name already exists"),
    USER_ALREADY_IN_GROUP("User already in group"),
    LOGIN_FAILED("Login failed");

    private final String message;

    private ErrorType(final String message) {
        this.message = message;
    }

    public String getStringValue() {
        return this.message;
    }

}
