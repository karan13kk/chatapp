package com.example.chatapp.utils.jwt.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.chatapp.dto.group.JwtResponseDto;
import com.example.chatapp.utils.jwt.JwtUtil;

@Service
public class JwtUtilSHA256impl implements JwtUtil {

    @Value("${secret.jwt.key}")
    private String secretKey;

    @Override
    public String signToken(String subject) {
        Algorithm algorithm = Algorithm.HMAC384(secretKey);
        long currentTime = new Date().getTime();
        long expiresAt = currentTime + 60 * 60 * 1000;
        return JWT.create().withExpiresAt(new Date(expiresAt)).withIssuedAt(new Date(currentTime))
                .withClaim("subject", subject)
                .sign(algorithm);
    }

    @Override
    public JwtResponseDto validateToken(String token) {
        System.out.println("TOKEN : " + token);
        Algorithm algorithm = Algorithm.HMAC384(secretKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decodedJWT = verifier.verify(token);
        return JwtResponseDto.builder().createAt(decodedJWT.getIssuedAt()).expireAt(decodedJWT.getExpiresAt())
                .subject(decodedJWT.getClaim("subject").asString()).build();
    }

}
