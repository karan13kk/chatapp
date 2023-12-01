package com.example.chatapp.dto.user;

import com.example.chatapp.enums.APIStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
    APIStatus status;
    String message;
    String accessToken;
}
