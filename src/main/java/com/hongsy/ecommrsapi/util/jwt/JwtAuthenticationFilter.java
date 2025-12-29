package com.hongsy.ecommrsapi.util.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
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
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException, java.io.IOException {
        // 요청 헤더에서 JWT 토큰 추출
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {

                if(redisTemplate.hasKey("blacklist:"+token)){
                    request.setAttribute("exception", "로그아웃된 토큰입니다.");
                }else {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", "토큰이 만료되었습니다. 다시 로그인해주세요.");
        } catch (SignatureException | MalformedJwtException e) {
            request.setAttribute("exception", "유효하지 않은 토큰입니다.");

        } catch (UnsupportedJwtException e) {
            request.setAttribute("exception", "지원하지 않는 토큰 형식입니다.");

        } catch (Exception e) {
            request.setAttribute("exception", "토큰 검증 중 오류가 발생했습니다.");
        }

        // 다음 필터로 요청 전달
        chain.doFilter(request, response);
    }
}
