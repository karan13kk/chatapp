package com.example.chatapp.dto.message;

import lombok.Data;

@Data
public class SendMessageRequestDto {
    String to;
    String text;
}
