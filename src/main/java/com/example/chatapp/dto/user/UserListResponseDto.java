package com.example.chatapp.dto.user;

import java.util.List;

import com.example.chatapp.enums.APIStatus;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserListResponseDto {
    APIStatus status;
    List<String> data;
}
