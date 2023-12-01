package com.example.chatapp.enums;

public enum APIStatus {
    SUCCESS("success"),
    FAILURE("failure");

    private final String apiStatus;

    private APIStatus(final String apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getStringValue() {
        return this.apiStatus;
    }
}
