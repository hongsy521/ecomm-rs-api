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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final AuthenticationManager authenticationManager;

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
}
