package com.example.chatapp.utils.jwt;

import com.example.chatapp.dto.group.JwtResponseDto;

public interface JwtUtil {
    String signToken(String subject);

    JwtResponseDto validateToken(String token);
}
