package com.hongsy.ecommrsapi.util.jwt;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String,String> redisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException, java.io.IOException {
        // 요청 헤더에서 JWT 토큰 추출
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        // 토큰 유효성 검사
        if (token != null) {

            if(!jwtTokenProvider.validateToken(token)){
                sendErrorResponse((HttpServletResponse) response, "유효하지 않거나 만료된 토큰입니다.");
                return;
            }

            // 로그아웃/회원탈퇴한 경우 블랙리스트 AT
            if(redisTemplate.hasKey("blacklist:" + token)){
                sendErrorResponse((HttpServletResponse) response, "이미 로그아웃된 토큰입니다.");
                return;
            }

            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청 전달
        chain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message)
        throws IOException, java.io.IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
