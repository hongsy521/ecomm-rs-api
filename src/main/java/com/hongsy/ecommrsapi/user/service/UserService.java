package com.hongsy.ecommrsapi.user.service;

import com.hongsy.ecommrsapi.user.dto.UserRequestDto;
import com.hongsy.ecommrsapi.user.entity.Gender;
import com.hongsy.ecommrsapi.user.entity.User;
import com.hongsy.ecommrsapi.user.repository.UserRepository;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(UserRequestDto userRequestDto) {
        Optional<User> existingUser = userRepository.findByEmail(userRequestDto.getEmail());

        if(existingUser.isPresent()){
            throw new CustomException(ErrorCode.EXISTING_USER);
        }
        Gender gender = Gender.fromKorean(userRequestDto.getGender());
        String encodePassword = passwordEncoder.encode(userRequestDto.getPassword());
        User user = User.createUser(userRequestDto,gender,encodePassword);

        userRepository.save(user);

    }
}
