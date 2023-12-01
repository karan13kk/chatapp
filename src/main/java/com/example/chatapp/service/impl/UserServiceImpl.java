package com.example.chatapp.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chatapp.entity.User;
import com.example.chatapp.exception.CustomRuntimeException;
import com.example.chatapp.exception.ErrorType;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.service.UserService;
import com.example.chatapp.utils.hash.SHA256Hash;
import com.example.chatapp.utils.jwt.JwtUtil;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SHA256Hash sha256Hash;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public void createUser(String userName, String passcode) {
        String userId = sha256Hash.getHash(userName);
        String passwordHash = sha256Hash.getHash(userId + passcode);
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isPresent()) {
            throw new CustomRuntimeException(ErrorType.DUPLICATE_USER_ERROR,
                    "user is not present in db while creating");
        }
        User userObject = User.builder().userId(userId).userName(userName).passcode(passwordHash).build();
        userRepository.save(userObject);
    }

    @Override
    public List<String> getUserList() {
        return userRepository.findAllUserName();
    }

    @Override
    public String loginUser(String userName, String passcode) {
        Long loginTime = new Date().getTime();
        loginTime -= loginTime % 1000;
        String userId = sha256Hash.getHash(userName);
        String passwordHash = sha256Hash.getHash(userId + passcode);
        Optional<User> user = userRepository.findByUserIdAndPasscode(userId, passwordHash);
        if (!user.isPresent()) {
            throw new CustomRuntimeException(ErrorType.LOGIN_FAILED, "user passcode combination is present in db");
        }
        user.get().setLastLoggedInTime(new Timestamp(loginTime));
        user.get().setLoggedIn(true);
        String token = jwtUtil.signToken(userId);
        userRepository.save(user.get());
        return token;
    }

    @Override
    public void logoutUser(String userName) {
        String userId = sha256Hash.getHash(userName);
        Optional<User> user = userRepository.findByUserId(userId);
        if (!user.isPresent()) {
            throw new CustomRuntimeException(ErrorType.LOGIN_FAILED, "user passcode combination is present in db");
        }
        user.get().setLoggedIn(false);
        userRepository.save(user.get());
    }

}
