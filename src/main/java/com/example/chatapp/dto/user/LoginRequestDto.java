package com.example.chatapp.dto.user;

import lombok.Data;

@Data
public class LoginRequestDto {
    String username;
    String passcode;
}
