package com.example.chatapp.dto.message;

import java.util.HashMap;
import java.util.List;

import com.example.chatapp.enums.APIStatus;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MessageHistoryResponseDto {
    APIStatus status;
    List<HashMap<String, String>> texts;
}
