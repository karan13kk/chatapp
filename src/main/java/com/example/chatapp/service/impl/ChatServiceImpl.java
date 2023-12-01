package com.example.chatapp.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chatapp.dto.message.UserMessageDto;
import com.example.chatapp.entity.Message;
import com.example.chatapp.entity.User;
import com.example.chatapp.entity.UserGroup;
import com.example.chatapp.exception.CustomRuntimeException;
import com.example.chatapp.exception.ErrorType;
import com.example.chatapp.repository.MessageRepository;
import com.example.chatapp.repository.UserGroupRepository;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.service.ChatService;
import com.example.chatapp.utils.hash.SHA256Hash;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserGroupRepository userGroupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SHA256Hash sha256Hash;

    @Override
    public void sendMessgage(String senderName, String receiverName, String message) {

        String groupId = getGroupId(senderName, receiverName);

        User sender = validateAndGetUserDetails(senderName);
        String senderId = sender.getUserId();

        String receiverId = sha256Hash.getHash(receiverName);
        Optional<User> receiver = userRepository.findByUserId(receiverId);
        if (!receiver.isPresent()) {
            throw new CustomRuntimeException(ErrorType.USER_NOT_FOUND,
                    "receiver user with name " + receiverName + " not present in db");
        }

        List<UserGroup> userGroup = userGroupRepository.findByGroupId(groupId);
        if (userGroup.size() == 0) {
            userGroupRepository.save(UserGroup.builder().groupId(groupId).lastMessageId(-1l).userId(senderId).build());
            userGroupRepository
                    .save(UserGroup.builder().groupId(groupId).lastMessageId(-1l).userId(receiverId).build());
        }

        messageRepository.save(
                Message.builder().senderId(senderId).senderName(senderName).groupId(groupId).text(message).build());

    }

    @Override
    public List<UserMessageDto> getUnreadMessages(String userName) {
        List<UserMessageDto> userMessageDtos = new ArrayList<>();
        User sender = validateAndGetUserDetails(userName);
        String userId = sender.getUserId();
        List<UserGroup> userGroupList = userGroupRepository.findByUserId(userId);
        for (UserGroup userGroup : userGroupList) {
            List<Message> messages = messageRepository.findByGroupIdAndMessageId(userGroup.getGroupId(),
                    userGroup.getLastMessageId());
            if (messages.size() == 0)
                continue;
            for (Message message : messages) {
                if (message.getSenderId().equals(userId))
                    continue;
                userMessageDtos
                        .add(UserMessageDto.builder().username(message.getSenderName()).text(message.getText())
                                .build());
            }
            long lastMessageId = messages.get(messages.size() - 1).getId();
            userGroup.setLastMessageId(lastMessageId);
            userGroupRepository.save(userGroup);
        }

        return userMessageDtos;
    }

    @Override
    public List<HashMap<String, String>> getChatHistoryOfSpecificUser(String primaryUserName, String friendUserName) {
        List<HashMap<String, String>> userChatMapList = new ArrayList<>();
        String groupId = getGroupId(primaryUserName, friendUserName);
        validateAndGetUserDetails(primaryUserName);
        List<Message> messages = messageRepository.findByGroupId(groupId);
        for (Message message : messages) {
            HashMap<String, String> userChatMap = new HashMap<>();
            userChatMap.put(message.getSenderName(), message.getText());
            userChatMapList.add(userChatMap);
        }
        return userChatMapList;
    }

    @Override
    public List<HashMap<String, String>> getChatHistoryOfSpecificGroup(String primaryUserName, String groupName) {
        List<HashMap<String, String>> userChatMapList = new ArrayList<>();
        String groupId = getGroupId("GROUP_", groupName);
        User user = validateAndGetUserDetails(primaryUserName);
        Optional<UserGroup> userGroup = userGroupRepository.findByUserIdAndGroupId(user.getUserId(), groupId);
        if (!userGroup.isPresent())
            throw new CustomRuntimeException(ErrorType.GROUP_NOT_FOUND,
                    String.format("Group %s not found for user %s", groupName, primaryUserName));
        List<Message> messages = messageRepository.findByGroupId(groupId);
        for (Message message : messages) {
            HashMap<String, String> userChatMap = new HashMap<>();
            userChatMap.put(message.getSenderName(), message.getText());
            userChatMapList.add(userChatMap);
        }
        return userChatMapList;
    }

    @Override
    public void registerInGroup(String userName, String groupName) {
        String groupId = getGroupId("GROUP_", groupName);

        User user = validateAndGetUserDetails(userName);
        String userId = user.getUserId();

        List<UserGroup> group = userGroupRepository.findByGroupId(groupId);
        if (group.size() == 0) {
            throw new CustomRuntimeException(ErrorType.GROUP_NOT_FOUND,
                    String.format("User %s registering in a non created group", userName));
        }
        Optional<UserGroup> userGroup = userGroupRepository.findByUserIdAndGroupId(userId, groupId);
        if (userGroup.isPresent()) {
            throw new CustomRuntimeException(ErrorType.USER_ALREADY_IN_GROUP,
                    String.format("User %s is already in a group", userName));
        }
        userGroupRepository.save(UserGroup.builder().groupId(groupId).lastMessageId(-1l).userId(userId).build());
    }

    @Override
    public void sendGroupMessgage(String senderName, String groupName, String message) {
        String groupId = getGroupId("GROUP_", groupName);

        User sender = validateAndGetUserDetails(senderName);
        String senderId = sender.getUserId();

        Optional<UserGroup> userGroup = userGroupRepository.findByUserIdAndGroupId(senderId, groupId);
        if (!userGroup.isPresent())
            throw new CustomRuntimeException(ErrorType.GROUP_NOT_FOUND,
                    String.format("Group %s not found for user %s", groupName, senderName));

        messageRepository.save(
                Message.builder().senderId(senderId).senderName(senderName).groupId(groupId).text(message).build());
    }

    @Override
    public void createGroup(String userName, String groupName) {
        String groupId = getGroupId("GROUP_", groupName);

        User user = validateAndGetUserDetails(userName);
        String userId = user.getUserId();

        List<UserGroup> userGroup = userGroupRepository.findByGroupId(groupId);
        if (userGroup.size() > 0)
            throw new CustomRuntimeException(ErrorType.DUPLICATE_GROUP_NAME,
                    String.format("Group %s already exist", groupName));

        userGroupRepository.save(UserGroup.builder().groupId(groupId).userId(userId).lastMessageId(-1l).build());
    }

    private String getGroupId(String user1, String user2) {
        if (user1.equals(user2))
            throw new CustomRuntimeException(ErrorType.DUPLICATE_USER_MESSAGE_ERROR,
                    String.format("User1 %s is duplicate with user2 %s", user1, user2));
        if (user1.compareTo(user2) > 0) {
            return sha256Hash.getHash(user1.concat(user2));
        }
        return sha256Hash.getHash(user2.concat(user1));
    }

    private User validateAndGetUserDetails(String userName) {
        String senderId = sha256Hash.getHash(userName);
        Optional<User> sender = userRepository.findByUserId(senderId);
        return sender.get();
    }

}
