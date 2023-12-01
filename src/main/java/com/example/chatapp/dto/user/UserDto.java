package com.example.chatapp.dto.user;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
    private String userId;

    private String userName;

    private String passcode;

    private boolean isLoggedIn;

    private Date lastLoggedInTime;

    private Date createdAt;

    private Date updatedAt;
}
