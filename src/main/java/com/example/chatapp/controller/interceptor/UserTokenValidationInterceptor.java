package com.example.chatapp.controller.interceptor;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.chatapp.controller.middleware.UserTokenValidation;
import com.example.chatapp.dto.group.JwtResponseDto;
import com.example.chatapp.entity.User;
import com.example.chatapp.exception.CustomRuntimeException;
import com.example.chatapp.exception.ErrorType;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.utils.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserTokenValidationInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            UserTokenValidationInterceptor.class);

    private static final String TOKEN_HEADER = "access-token";

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            UserTokenValidation annotation = handlerMethod.getMethod().getAnnotation(UserTokenValidation.class);
            if (annotation != null) {
                String token = request.getHeader(TOKEN_HEADER);
                if (token == null) {
                    throw new CustomRuntimeException(ErrorType.AUTHENTICATION_ERROR, "Token not provided");
                }
                try {
                    JwtResponseDto jwtResponseDto = jwtUtil.validateToken(token);
                    String userId = jwtResponseDto.getSubject();
                    if (jwtResponseDto.getExpireAt().getTime() < new Date().getTime()) {
                        throw new CustomRuntimeException(ErrorType.AUTHENTICATION_ERROR, "Token has already expired");
                    }
                    Optional<User> user = userRepository.findByUserId(userId);
                    if (!user.isPresent())
                        throw new CustomRuntimeException(ErrorType.USER_NOT_FOUND, "User is not present");
                    if (!user.get().isLoggedIn()) {
                        throw new CustomRuntimeException(ErrorType.AUTHENTICATION_ERROR, userId);
                    }
                    if (jwtResponseDto.getCreateAt().getTime() < user.get().getLastLoggedInTime().getTime()) {
                        throw new CustomRuntimeException(ErrorType.AUTHENTICATION_ERROR,
                                "Last login time is greater than token generated time");
                    }
                    request.setAttribute("access-token", user.get().getUserName());
                } catch (JWTVerificationException exception) {
                    LOGGER.error("ERROR JWT VERIFICATION", exception);
                    throw new CustomRuntimeException(ErrorType.AUTHENTICATION_ERROR, "Invalid token");
                }

            }
        }
        return true;
    }
}
