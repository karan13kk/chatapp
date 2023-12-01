package com.example.chatapp.dto.message;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserMessageDto {
    String username;
    String text;
}
