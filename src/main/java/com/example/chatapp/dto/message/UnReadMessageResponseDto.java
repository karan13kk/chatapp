package com.example.chatapp.dto.message;

import java.util.List;

import com.example.chatapp.enums.APIStatus;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UnReadMessageResponseDto {
    APIStatus status;
    String message;
    List<UserMessageDto> data;
}
