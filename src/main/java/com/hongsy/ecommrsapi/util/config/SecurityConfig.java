package com.hongsy.ecommrsapi.util.config;

import com.hongsy.ecommrsapi.util.common.CustomAuthenticationEntryPoint;
import com.hongsy.ecommrsapi.util.jwt.JwtAuthenticationFilter;
import com.hongsy.ecommrsapi.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String,String> redisTemplate;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/user/signup","/api/user/login","/api/product/**","/swagger-ui/**","/v3/api-docs/**","/actuator/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/seller/**").hasRole("SELLER")
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            // 세션 비활성화
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // UsernamePasswordAuthenticationFilter 이전에 JWT 인증 필터 실행
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider,redisTemplate),
                UsernamePasswordAuthenticationFilter.class
            )

            .exceptionHandling(exception-> exception.authenticationEntryPoint(
                customAuthenticationEntryPoint));

        return http.build();
    }
}

