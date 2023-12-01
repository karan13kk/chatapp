package com.example.chatapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatapp.controller.middleware.UserTokenValidation;
import com.example.chatapp.dto.user.LoginRequestDto;
import com.example.chatapp.dto.user.LoginResponseDto;
import com.example.chatapp.dto.user.RegisterUserRequestDto;
import com.example.chatapp.dto.user.RegisterUserResponseDto;
import com.example.chatapp.dto.user.UserListResponseDto;
import com.example.chatapp.enums.APIStatus;
import com.example.chatapp.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class UserController {

        @Autowired
        UserService userService;

        @PostMapping("/user")
        public ResponseEntity<RegisterUserResponseDto> getAccount(
                        @RequestBody @Validated RegisterUserRequestDto registerUserRequestDto) {
                userService.createUser(registerUserRequestDto.getUsername(), registerUserRequestDto.getPasscode());
                RegisterUserResponseDto registerUserResponseDto = RegisterUserResponseDto.builder()
                                .status(APIStatus.SUCCESS)
                                .message("User created successfully").build();
                return ResponseEntity.status(200).body(registerUserResponseDto);
        }

        @GetMapping("/user")
        public ResponseEntity<UserListResponseDto> getAllUsersList() {
                List<String> response = userService.getUserList();
                UserListResponseDto userListResponseDto = UserListResponseDto.builder().status(APIStatus.SUCCESS)
                                .data(response).build();
                return ResponseEntity.status(200).body(userListResponseDto);
        }

        @PostMapping("/login")
        public ResponseEntity<LoginResponseDto> loginUser(
                        @RequestBody @Validated LoginRequestDto registerUserRequestDto) {
                String accessToken = userService.loginUser(registerUserRequestDto.getUsername(),
                                registerUserRequestDto.getPasscode());
                LoginResponseDto loginResponseDto = LoginResponseDto.builder().status(APIStatus.SUCCESS)
                                .message("User logged in successfully").accessToken(accessToken).build();
                return ResponseEntity.status(200).body(loginResponseDto);
        }

        @PostMapping("/logout")
        @UserTokenValidation()
        public ResponseEntity<RegisterUserResponseDto> logoutUser(
                        HttpServletRequest request,
                        @RequestHeader("access-token") String accessToken) {
                String userName = (String) request.getAttribute("access-token");
                userService.logoutUser(userName);
                RegisterUserResponseDto registerUserResponseDto = RegisterUserResponseDto.builder()
                                .status(APIStatus.SUCCESS)
                                .message("User logged out successfully").build();
                return ResponseEntity.status(200).body(registerUserResponseDto);
        }
}
