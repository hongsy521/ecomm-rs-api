package com.hongsy.ecommrsapi.user.controller;

import com.hongsy.ecommrsapi.user.dto.LoginRequestDto;
import com.hongsy.ecommrsapi.user.dto.SignupRequestDto;
import com.hongsy.ecommrsapi.user.service.UserService;
import com.hongsy.ecommrsapi.util.UserDetailsImpl;
import com.hongsy.ecommrsapi.util.common.CommonResponse;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import com.hongsy.ecommrsapi.util.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "사용자 API")
@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signup(
        @Valid @RequestBody SignupRequestDto signupRequestDto) {
        userService.signup(signupRequestDto);
        return ResponseEntity.ok(new CommonResponse<>("회원가입이 완료되었습니다.", 200, ""));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<CommonResponse> login(@Valid @RequestBody LoginRequestDto loginRequestDto,
        HttpServletResponse response) {
        String accessToken = userService.login(loginRequestDto, response);
        return ResponseEntity.ok(new CommonResponse<>("로그인이 완료되었습니다.", 200, accessToken));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(
        HttpServletRequest request,
        @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        String accessToken = jwtTokenProvider.resolveToken(request);

        userService.logout(accessToken, refreshToken);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
            .path("/")
            .httpOnly(true)
            .secure(true)
            .maxAge(0)
            .sameSite("None")
            .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
            .body(new CommonResponse<>("로그아웃이 완료되었습니다.", 200, ""));
    }

    @Operation(summary = "회원탈퇴")
    @PostMapping("/withdraw")
    public ResponseEntity<CommonResponse> withdraw(@AuthenticationPrincipal UserDetailsImpl userDetails,HttpServletRequest request,@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        userService.withdraw(userDetails.getId(),accessToken,refreshToken);
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
            .path("/")
            .httpOnly(true)
            .secure(true)
            .maxAge(0)
            .sameSite("None")
            .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
            .body(new CommonResponse<>("회원탈퇴가 완료되었습니다.", 200, ""));
    }

    @Operation(summary = "액세스토큰/리프레시토큰 재발행")
    @PostMapping("/reissue-token")
    public ResponseEntity<CommonResponse> reissueToken(@CookieValue(value = "refreshToken")String refreshToken,HttpServletResponse response) {
        String newAccessToken = userService.reissueToken(refreshToken,response);
        return ResponseEntity.ok(new CommonResponse<>("액세스 토큰 재발행이 완료되었습니다.", 200, newAccessToken));
    }
}
