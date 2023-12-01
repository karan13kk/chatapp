package com.example.chatapp.service;

import java.util.List;

public interface UserService {
    void createUser(String userName, String passcode);

    List<String> getUserList();

    String loginUser(String userName, String passcode);

    void logoutUser(String userName);
}
