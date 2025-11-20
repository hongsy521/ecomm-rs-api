package com.hongsy.ecommrsapi.util.jwt;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException, java.io.IOException {
        // 요청 헤더에서 JWT 토큰 추출
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        // 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 토큰이 유효하면, Authentication 객체 생성
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // SecurityContextHolder에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청 전달
        chain.doFilter(request, response);
    }
}
