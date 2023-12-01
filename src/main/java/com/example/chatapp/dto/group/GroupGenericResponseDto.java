package com.example.chatapp.dto.group;

import com.example.chatapp.enums.APIStatus;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GroupGenericResponseDto {
    APIStatus status;
}
