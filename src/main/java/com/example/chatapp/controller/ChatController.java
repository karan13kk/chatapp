package com.example.chatapp.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatapp.controller.middleware.UserTokenValidation;
import com.example.chatapp.dto.group.CreateGroupDto;
import com.example.chatapp.dto.group.GroupGenericResponseDto;
import com.example.chatapp.dto.message.MessageHistoryResponseDto;
import com.example.chatapp.dto.message.SendGroupMessageRequestDto;
import com.example.chatapp.dto.message.SendMessageRequestDto;
import com.example.chatapp.dto.message.SendMessageResponseDto;
import com.example.chatapp.dto.message.UnReadMessageResponseDto;
import com.example.chatapp.dto.message.UserMessageDto;
import com.example.chatapp.enums.APIStatus;
import com.example.chatapp.service.ChatService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ChatController {
        @Autowired
        ChatService chatService;

        @PostMapping("/user/message")
        @UserTokenValidation()
        public ResponseEntity<SendMessageResponseDto> sendMessage(
                        HttpServletRequest request,
                        @RequestHeader("access-token") String accessToken,
                        @RequestBody @Validated SendMessageRequestDto sendMessageRequestDto) {
                String senderName = (String) request.getAttribute("access-token");
                chatService.sendMessgage(senderName, sendMessageRequestDto.getTo(), sendMessageRequestDto.getText());
                SendMessageResponseDto sendMessageResponseDto = SendMessageResponseDto.builder()
                                .status(APIStatus.SUCCESS)
                                .build();
                return ResponseEntity.status(200).body(sendMessageResponseDto);
        }

        @GetMapping("/user/message")
        @UserTokenValidation()
        public ResponseEntity<UnReadMessageResponseDto> getListOfUnreadMessages(
                        HttpServletRequest request,
                        @RequestHeader("access-token") String accessToken) {
                String userName = (String) request.getAttribute("access-token");
                List<UserMessageDto> userUnreadMessages = chatService.getUnreadMessages(userName);
                UnReadMessageResponseDto unReadMessageResponseDto = UnReadMessageResponseDto.builder()
                                .status(APIStatus.SUCCESS)
                                .data(userUnreadMessages).message("You have message(s)").build();
                return ResponseEntity.status(200).body(unReadMessageResponseDto);
        }

        @GetMapping("/user/message/history")
        @UserTokenValidation()
        public ResponseEntity<MessageHistoryResponseDto> getHistoryOfSpecificUserMessage(
                        HttpServletRequest request,
                        @RequestHeader("access-token") String accessToken, @Param("friend") String friend) {
                String userName = (String) request.getAttribute("access-token");
                List<HashMap<String, String>> chatHistoryOfSpecificUser = chatService.getChatHistoryOfSpecificUser(
                                userName,
                                friend);
                MessageHistoryResponseDto messageHistoryResponseDto = MessageHistoryResponseDto.builder()
                                .status(APIStatus.SUCCESS)
                                .texts(chatHistoryOfSpecificUser).build();
                return ResponseEntity.status(200).body(messageHistoryResponseDto);
        }

        @GetMapping("/group/message/history")
        @UserTokenValidation()
        public ResponseEntity<MessageHistoryResponseDto> getHistoryOfSpecificGroupMessage(
                        HttpServletRequest request,
                        @RequestHeader("access-token") String accessToken,
                        @Param("groupName") String groupName) {
                String userName = (String) request.getAttribute("access-token");
                List<HashMap<String, String>> chatHistoryOfSpecificUser = chatService.getChatHistoryOfSpecificGroup(
                                userName,
                                groupName);
                MessageHistoryResponseDto messageHistoryResponseDto = MessageHistoryResponseDto.builder()
                                .status(APIStatus.SUCCESS)
                                .texts(chatHistoryOfSpecificUser).build();
                return ResponseEntity.status(200).body(messageHistoryResponseDto);
        }

        @PostMapping("/group/{groupName}/message")
        @UserTokenValidation()
        public ResponseEntity<GroupGenericResponseDto> sendGroupMessage(
                        HttpServletRequest request,
                        @RequestHeader("access-token") String accessToken,
                        @PathVariable("groupName") String groupName,
                        @RequestBody @Validated SendGroupMessageRequestDto sendMessageRequestDto) {
                String senderName = (String) request.getAttribute("access-token");
                chatService.sendGroupMessgage(senderName, groupName, sendMessageRequestDto.getText());
                GroupGenericResponseDto sendMessageResponseDto = GroupGenericResponseDto.builder()
                                .status(APIStatus.SUCCESS)
                                .build();
                return ResponseEntity.status(200).body(sendMessageResponseDto);
        }

        @PostMapping("/group/{groupName}/register")
        @UserTokenValidation()
        public ResponseEntity<GroupGenericResponseDto> registerInGroup(
                        HttpServletRequest request,
                        @RequestHeader("access-token") String accessToken,
                        @PathVariable("groupName") String groupName) {
                String senderName = (String) request.getAttribute("access-token");
                chatService.registerInGroup(senderName, groupName);
                GroupGenericResponseDto sendMessageResponseDto = GroupGenericResponseDto.builder()
                                .status(APIStatus.SUCCESS)
                                .build();
                return ResponseEntity.status(200).body(sendMessageResponseDto);
        }

        @PostMapping("/group/create")
        @UserTokenValidation()
        public ResponseEntity<GroupGenericResponseDto> createGroup(
                        HttpServletRequest request,
                        @RequestHeader("access-token") String accessToken,
                        @RequestBody @Validated CreateGroupDto createGroupDto) {
                String senderName = (String) request.getAttribute("access-token");
                chatService.createGroup(senderName, createGroupDto.getGroupName());
                GroupGenericResponseDto sendMessageResponseDto = GroupGenericResponseDto.builder()
                                .status(APIStatus.SUCCESS)
                                .build();
                return ResponseEntity.status(200).body(sendMessageResponseDto);
        }
}
