package com.example.chatapp.utils.hash.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.chatapp.utils.hash.SHA256Hash;

@Component
public class LocalSHA256Hash implements SHA256Hash {

    @Value("${secret.hash.salt}")
    private String hashSalt;

    @Override
    public String getHash(String input) {
        String saltedPassword = hashSalt + input;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Error in hashing");
        }
        byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();

    }
}
