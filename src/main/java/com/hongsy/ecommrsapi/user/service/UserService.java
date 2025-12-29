package com.hongsy.ecommrsapi.user.service;

import com.hongsy.ecommrsapi.user.dto.LoginRequestDto;
import com.hongsy.ecommrsapi.user.dto.SignupRequestDto;
import com.hongsy.ecommrsapi.user.entity.Gender;
import com.hongsy.ecommrsapi.user.entity.Role;
import com.hongsy.ecommrsapi.user.entity.User;
import com.hongsy.ecommrsapi.user.repository.UserRepository;
import com.hongsy.ecommrsapi.util.UserDetailsImpl;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import com.hongsy.ecommrsapi.util.jwt.JwtTokenProvider;
import com.hongsy.ecommrsapi.util.jwt.RefreshToken;
import com.hongsy.ecommrsapi.util.jwt.RefreshTokenRedisRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        Optional<User> existingUser = userRepository.findByEmail(signupRequestDto.getEmail());

        if (existingUser.isPresent()) {
            throw new CustomException(ErrorCode.EXISTING_USER);
        }
        Gender gender = Gender.genderFromKorean(signupRequestDto.getGender());
        Set<Role> roleSet = Role.roleFromKorean(signupRequestDto.getRoles());
        String encodePassword = passwordEncoder.encode(signupRequestDto.getPassword());
        User user = User.createUser(signupRequestDto, gender, encodePassword, roleSet);

        userRepository.save(user);

    }

    public String login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword()
            )
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        String accessToken = jwtTokenProvider.createAccessToken(userId, roles);
        String refreshTokenString = jwtTokenProvider.createRefreshToken(userId);

        String jti = jwtTokenProvider.getJti(refreshTokenString);

        RefreshToken refreshToken = new RefreshToken(
            jti,
            userId,
            refreshTokenString
        );
        refreshTokenRedisRepository.save(refreshToken);
        addRefreshTokenToCookie(refreshTokenString, response);

        return "Bearer " + accessToken;
    }

    private void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);

        cookie.setHttpOnly(true);
        cookie.setMaxAge(1209600);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {

        // RT가 null이거나 이미 만료된 유효하지 않은 토큰일 경우에는 redis에서 삭제해줄 필요 없음
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String jti = jwtTokenProvider.getJti(refreshToken);
            refreshTokenRedisRepository.deleteById(jti);
        }

        if (accessToken != null) {
            Long expiration = jwtTokenProvider.getExpiration(accessToken);
            if (expiration > 0) {
                redisTemplate.opsForValue()
                    .set("blacklist:" + accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Transactional
    public void withdraw(Long userId, String accessToken, String refreshToken) {
        User user = findById(userId);

        // status 변경 후 softDelete
        user.editStatusByWithdrawn();

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String jti = jwtTokenProvider.getJti(refreshToken);
            refreshTokenRedisRepository.deleteById(jti);
        }
        if (accessToken != null) {
            Long expiration = jwtTokenProvider.getExpiration(accessToken);
            if (expiration > 0) {
                redisTemplate.opsForValue()
                    .set("blacklist:" + accessToken, "withdrawn", expiration,
                        TimeUnit.MILLISECONDS);
            }
        }
    }

    @Transactional
    public String reissueToken(String refreshToken, HttpServletResponse response) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        String jti = jwtTokenProvider.getJti(refreshToken);
        RefreshToken originalRefreshToken = refreshTokenRedisRepository.findById(jti).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_TOKEN)
        );

        String userId = jwtTokenProvider.getStringUserId(refreshToken);
        User user = findById(Long.valueOf(userId));

        List<String> roles = user.getRoles().stream()
            .map(roleEnum -> roleEnum.name())
            .collect(Collectors.toList());

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), roles);

        // RTR
        String newRefreshTokenString = jwtTokenProvider.createRefreshToken(user.getId());

        String newJti = jwtTokenProvider.getJti(newRefreshTokenString);
        RefreshToken newRefreshToken = new RefreshToken(newJti, user.getId(),
            newRefreshTokenString);
        refreshTokenRedisRepository.delete(originalRefreshToken);
        refreshTokenRedisRepository.save(newRefreshToken);
        addRefreshTokenToCookie(newRefreshTokenString, response);

        return "Bearer " + newAccessToken;
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
    }
}
