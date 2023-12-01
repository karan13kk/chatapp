package com.example.chatapp.controller;

import java.util.HashMap;

import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.example.chatapp.AbstractIntegrationTest;
import com.example.chatapp.dto.group.CreateGroupDto;
import com.example.chatapp.dto.message.SendGroupMessageRequestDto;
import com.example.chatapp.dto.message.SendMessageRequestDto;
import com.example.chatapp.dto.user.LoginRequestDto;
import com.example.chatapp.dto.user.RegisterUserRequestDto;
import com.example.chatapp.enums.APIStatus;
import com.example.chatapp.exception.ErrorType;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql({ "classpath:/db/changelog/user.sql" })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestUserController extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final String ACCESS_TOKEN = "eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJleHAiOjI3MDE0NjU2NzIsImlhdCI6MTYwMTQ2MjA3Miwic3ViamVjdCI6Ijg4ZDkzZWFhMWRjYmE4MzY0YmIxYzc5N2I5NDNiZjEyMWIzMzY5MDRkMTJlNWIyNmIxNTFhYTQzZmIzYzljYjIifQ.1f87MNYBsoJ4oF2XOhXJAohFfTWTqRfvW9rVgt-z7tgeVtNZ4u922AAoh60Gu4o_";

    @Test
    @Order(1)
    public void createUser_Success() throws Exception {
        RegisterUserRequestDto requestDto = new RegisterUserRequestDto();
        requestDto.setUsername("arjun");
        requestDto.setPasscode("1234");
        String content = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User created successfully"));
    }

    @Test
    @Order(2)
    public void getAllUsersList_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @Order(3)
    public void loginUser_Success_And_Logout_Success() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setUsername("karan");
        requestDto.setPasscode("1234");
        SendMessageRequestDto requestSendMessageDto = new SendMessageRequestDto();
        requestSendMessageDto.setTo("John Doe");
        requestSendMessageDto.setText("Hello, this is a test message.");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/user/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestSendMessageDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("failure"));
        String json = mockMvc.perform(MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User logged in successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
                .andReturn()
                .getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> map = mapper.readValue(json, HashMap.class);
        String accessToken = map.get("accessToken");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/user/message")
                .header("access-token", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestSendMessageDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"));
        mockMvc.perform(MockMvcRequestBuilders
                .post("/user/message")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestSendMessageDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("failure"));

        SendMessageRequestDto requestSendWrongMessageDto = new SendMessageRequestDto();
        requestSendWrongMessageDto.setTo("John Doe 1");
        requestSendWrongMessageDto.setText("Hello, this is a test message.");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/user/message")
                .header("access-token", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestSendWrongMessageDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("failure"));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/user/message")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("You have message(s)"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/user/message/history?friend=karan")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.texts").isArray());
        mockMvc.perform(MockMvcRequestBuilders
                .post("/logout")
                .header("access-token", accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(APIStatus.SUCCESS.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("User logged out successfully"));
        mockMvc.perform(MockMvcRequestBuilders
                .post("/user/message")
                .header("access-token", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestSendMessageDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("failure"));
    }

    @Test
    @Order(4)
    public void logoutUser_Failure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/logout")
                .header("access-token", "random_token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(APIStatus.FAILURE.getStringValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(ErrorType.AUTHENTICATION_ERROR.getStringValue()));
    }

    @Test
    @Order(5)
    public void createGroup_Success() throws Exception {
        CreateGroupDto requestDto = new CreateGroupDto();
        requestDto.setGroupName("ved");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/group/create")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @Order(6)
    public void registerInGroup_Failure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/group/ved1/register")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("failure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Group not found"));
    }

    @Test
    @Order(7)
    public void sendMessage_Success() throws Exception {
        SendMessageRequestDto requestDto = new SendMessageRequestDto();
        requestDto.setTo("karan");
        requestDto.setText("Hello, this is a test message.");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/user/message")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @Order(8)
    public void getListOfUnreadMessages_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/user/message")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("You have message(s)"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
    }

    @Test
    @Order(9)
    public void getHistoryOfSpecificUserMessage_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/user/message/history?friend=karan")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.texts").isArray());
    }

    @Test
    @Order(10)
    public void getHistoryOfSpecificGroupMessage_Success() throws Exception {
        CreateGroupDto requestDto = new CreateGroupDto();
        requestDto.setGroupName("ved3");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/group/create")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/group/message/history?groupName=ved3")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.texts").isArray());
    }

    @Test
    @Order(11)
    public void sendGroupMessage_Success() throws Exception {
        CreateGroupDto requestCreateGroupDto = new CreateGroupDto();
        requestCreateGroupDto.setGroupName("ved2");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/group/create")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestCreateGroupDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"));
        SendGroupMessageRequestDto requestDto = new SendGroupMessageRequestDto();
        requestDto.setText("Hello, this is a test group message.");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/group/ved2/message")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @Order(11)
    public void run_All_In_Order() throws Exception {
        CreateGroupDto requestCreateGroupDto = new CreateGroupDto();
        requestCreateGroupDto.setGroupName("ved5");
        LoginRequestDto requestLoginDto = new LoginRequestDto();
        requestLoginDto.setUsername("karan");
        requestLoginDto.setPasscode("1234");
        String json = mockMvc.perform(MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestLoginDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User logged in successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
                .andReturn()
                .getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> map = mapper.readValue(json, HashMap.class);
        String accessToken = map.get("accessToken");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/group/create")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestCreateGroupDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"));
        mockMvc.perform(MockMvcRequestBuilders
                .post("/group/create")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestCreateGroupDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("failure"));
        SendGroupMessageRequestDto requestDto = new SendGroupMessageRequestDto();
        requestDto.setText("Hello, this is a test group message.");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/group/ved5/register")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("failure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User already in group"));
        mockMvc.perform(MockMvcRequestBuilders
                .post("/group/ved5/register")
                .header("access-token", accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"));
        mockMvc.perform(MockMvcRequestBuilders
                .post("/group/ved5/message")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"));
        mockMvc.perform(MockMvcRequestBuilders
                .post("/group/ved6/message")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("failure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Group not found"));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/group/message/history?groupName=ved5")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.texts").isArray());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/group/message/history?groupName=ved7")
                .header("access-token", ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("failure"));
    }

}