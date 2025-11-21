package com.hongsy.ecommrsapi.user.service;

import com.hongsy.ecommrsapi.user.dto.LoginRequestDto;
import com.hongsy.ecommrsapi.user.dto.SignupRequestDto;
import com.hongsy.ecommrsapi.user.entity.Gender;
import com.hongsy.ecommrsapi.user.entity.Role;
import com.hongsy.ecommrsapi.user.entity.User;
import com.hongsy.ecommrsapi.user.repository.UserRepository;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import com.hongsy.ecommrsapi.util.jwt.JwtTokenProvider;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void createUser(SignupRequestDto signupRequestDto) {
        Optional<User> existingUser = userRepository.findByEmail(signupRequestDto.getEmail());

        if(existingUser.isPresent()){
            throw new CustomException(ErrorCode.EXISTING_USER);
        }
        Gender gender = Gender.genderFromKorean(signupRequestDto.getGender());
        Set<Role> roleSet = Role.roleFromKorean(signupRequestDto.getRoles());
        String encodePassword = passwordEncoder.encode(signupRequestDto.getPassword());
        User user = User.createUser(signupRequestDto,gender,encodePassword,roleSet);

        userRepository.save(user);

    }

    /*public void login(LoginRequestDto loginRequestDto) {
        String accessToken = jwtTokenProvider.creatAccessToken();
        String refreshToken = jwtTokenProvider.creatRefreshToken();
    }*/
}
