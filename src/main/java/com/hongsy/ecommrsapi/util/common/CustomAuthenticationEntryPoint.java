package com.hongsy.ecommrsapi.util.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {

        String exceptionMessage = (String) request.getAttribute("exception");
        String message = exceptionMessage != null ? exceptionMessage : "인증이 필요한 요청입니다.";

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
            .message(message)
            .error("Unauthorized")
            .statusCode(401)
            .timestamp(LocalDateTime.now())
            .build();

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
