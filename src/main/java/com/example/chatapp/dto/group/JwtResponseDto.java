package com.example.chatapp.dto.group;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JwtResponseDto {
    Date expireAt;
    Date createAt;
    String subject;
}
