package com.example.chatapp.dto.message;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MessageDto {

    private Long id;
    private String senderId;
    private String groupId;
    private String text;
    private Date createdAt;
    private Date updatedAt;

}