package com.example.chatapp.controller.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.chatapp.dto.api.APIResponse;
import com.example.chatapp.enums.APIStatus;
import com.example.chatapp.exception.CustomRuntimeException;

@ControllerAdvice("com.example.chatapp")
public class ControllerAdvisor {

        private static final Logger LOGGER = LoggerFactory.getLogger(
                        ControllerAdvisor.class);

        @ExceptionHandler(CustomRuntimeException.class)
        public ResponseEntity<APIResponse> handleCustomRuntimeException(
                        CustomRuntimeException customRuntimeException,
                        WebRequest request) {
                LOGGER.error("Controller advisor ::CustomRuntimeException failed for "
                                + customRuntimeException.getErrorType() + " with message: "
                                + customRuntimeException.getMessage(), customRuntimeException);
                APIResponse apiResponse = new APIResponse(APIStatus.FAILURE,
                                customRuntimeException.getErrorType().getStringValue());
                return ResponseEntity.status(200)
                                .body(apiResponse);
        }
}
