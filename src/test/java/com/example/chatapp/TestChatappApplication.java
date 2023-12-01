package com.example.chatapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestChatappApplication {

	public static void main(String[] args) {
		SpringApplication.from(ChatappApplication::main).with(TestChatappApplication.class).run(args);
	}

}
