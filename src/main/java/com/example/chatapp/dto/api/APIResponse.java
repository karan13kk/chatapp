package com.example.chatapp.dto.api;

import com.example.chatapp.enums.APIStatus;

import lombok.Data;

@Data
public class APIResponse {
    private String status;
    private String message = null;

    public APIResponse(APIStatus status, String message) {
        this.status = status.getStringValue();
        this.message = message;
    }
}