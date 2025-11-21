package com.hongsy.ecommrsapi.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final UserDetailsService userDetailsService;

    // 로그인 후 토큰 생성
    public String createAccessToken(Long userId, List<String> roles){
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("roles",roles);
        Date now = new Date();

        long accessTokenValidTime = 30 * 60 * 1000L; // 30분

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + accessTokenValidTime))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();

    }

    public String createRefreshToken(Long userId){
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        Date now = new Date();

        // 고유 식별자 JTI (DB/Redis 저장소 매핑 키로 사용)
        claims.setId(UUID.randomUUID().toString());

        long refreshTokenValidTime = 14 * 24 * 60 * 60 * 1000L; // 2주

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();

    }

    public String getJti(String refreshToken){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken).getBody().getId();
    }

    // 요청 헤더에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 유효성, 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 사용자 정보 추출
    public String getStringUserId(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        // UserDetailsService를 통해 사용자 정보를 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getStringUserId(token));

        // Spring Security의 Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


}
