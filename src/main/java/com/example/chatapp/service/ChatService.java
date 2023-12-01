package com.example.chatapp.service;

import java.util.HashMap;
import java.util.List;

import com.example.chatapp.dto.message.UserMessageDto;

public interface ChatService {
    void sendMessgage(String senderName, String receiverName, String message);

    void sendGroupMessgage(String senderName, String groupName, String message);

    void registerInGroup(String userName, String groupName);

    void createGroup(String userName, String groupName);

    List<UserMessageDto> getUnreadMessages(String userName);

    List<HashMap<String, String>> getChatHistoryOfSpecificUser(String primaryUserName, String friendUserName);

    List<HashMap<String, String>> getChatHistoryOfSpecificGroup(String primaryUserName, String groupName);
}
