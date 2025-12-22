package com.hongsy.ecommrsapi.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hongsy.ecommrsapi.user.dto.LoginRequestDto;
import com.hongsy.ecommrsapi.user.dto.SignupRequestDto;
import com.hongsy.ecommrsapi.user.service.UserService;
import com.hongsy.ecommrsapi.util.config.SecurityConfig;
import com.hongsy.ecommrsapi.util.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(controllers = UserController.class)
@Import({JwtTokenProvider.class, SecurityConfig.class})
@DisplayName("UserController Mockmvc 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("T1-(1). 회원가입 성공 테스트 - 회원가입 성공과 200 OK를 반환한다.")
    void signup_ShouldSuccess() throws Exception {
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .email("test@test.com")
            .password("password1234")
            .name("tester")
            .age(30)
            .gender("여성")
            .phoneNumber("010-1234-5678")
            .address("경기도 수원시")
            .roles(Set.of("판매자", "구매자"))
            .build();

        mockMvc.perform(
                post("/api/user/signup").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestDto))).andDo(print())
            .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."));

        verify(userService, times(1)).signup(any(SignupRequestDto.class));
    }

    @Test
    @DisplayName("T1-(2). 회원가입 실패 테스트 - validation 오류 400 Bad Request를 반환하고 서비스를 호출하지 않는다.")
    void signup_ShouldFail_WhenRequestIsInvalid() throws Exception {
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .email("test@test.com")
            .password("password1234")
            .name("tester")
            .age(30)
            .gender("여성")
            .phoneNumber("010-1234-5678")
            .address("경기도 수원시")
            .roles(Set.of())
            .build();

        mockMvc.perform(
                post("/api/user/signup").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestDto))).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("역할을 하나 이상 입력해주세요."));

        verify(userService, never()).signup(any(SignupRequestDto.class));
    }

    @Test
    @DisplayName("T2-(1). 로그인 성공 테스트 - accessToken과 200 OK를 반환한다.")
    void login_ShouldSuccess() throws Exception {
        String accessToken = "returnedAccessToken";
        LoginRequestDto requestDto = LoginRequestDto.builder()
            .email("test@test.com")
            .password("password1234")
            .build();

        given(userService.login(any(LoginRequestDto.class), any(HttpServletResponse.class))).willReturn(accessToken);

        mockMvc.perform(
                post("/api/user/login").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestDto))).andDo(print())
            .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("로그인이 완료되었습니다."))
            .andExpect(jsonPath("$.result").value(accessToken));

        verify(userService, times(1)).login(any(LoginRequestDto.class),
            any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("T2-(2). 로그인 실패 테스트 - validation 오류 400 Bad Request를 반환하고 서비스를 호출하지 않는다.")
    void login_ShouldFail_WhenRequestIsInvalid() throws Exception {
        LoginRequestDto requestDto = LoginRequestDto.builder()
            .email("test.com")
            .password("password1234")
            .build();

        mockMvc.perform(
                post("/api/user/login").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestDto))).andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("유효하지 않은 이메일 형식입니다."));

        verify(userService, never()).login(any(LoginRequestDto.class),
            any(HttpServletResponse.class));
    }

}