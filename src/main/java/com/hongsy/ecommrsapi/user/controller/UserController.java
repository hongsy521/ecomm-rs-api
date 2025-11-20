package com.hongsy.ecommrsapi.user.controller;

import com.hongsy.ecommrsapi.user.dto.UserRequestDto;
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
    public ResponseEntity<CommonResponse> createUser(@RequestBody UserRequestDto userRequestDto){
        userService.createUser(userRequestDto);
        return ResponseEntity.ok(new CommonResponse<>("회원가입이 완료되었습니다.",200,""));
    }
}
