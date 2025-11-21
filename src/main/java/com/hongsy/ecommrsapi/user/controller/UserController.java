package com.hongsy.ecommrsapi.user.controller;

import com.hongsy.ecommrsapi.user.dto.LoginRequestDto;
import com.hongsy.ecommrsapi.user.dto.SignupRequestDto;
import com.hongsy.ecommrsapi.user.service.UserService;
import com.hongsy.ecommrsapi.util.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> createUser(@RequestBody SignupRequestDto signupRequestDto){
        userService.createUser(signupRequestDto);
        return ResponseEntity.ok(new CommonResponse<>("회원가입이 완료되었습니다.",200,""));
    }

    /*@PostMapping("/login")
    public ResponseEntity<CommonResponse> login(@RequestBody LoginRequestDto loginRequestDto){
        userService.login(loginRequestDto);
        return ResponseEntity.ok(new CommonResponse<>("로그인이 완료되었습니다.",200,""));
    }*/
}
