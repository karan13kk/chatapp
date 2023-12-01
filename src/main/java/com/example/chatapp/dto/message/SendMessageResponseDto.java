package com.example.chatapp.dto.message;

import com.example.chatapp.enums.APIStatus;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SendMessageResponseDto {
    APIStatus status;
}
