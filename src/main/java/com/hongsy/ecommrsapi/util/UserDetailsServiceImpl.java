package com.hongsy.ecommrsapi.util;

import com.hongsy.ecommrsapi.user.entity.User;
import com.hongsy.ecommrsapi.user.repository.UserRepository;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;

        try {
            // 토큰을 통한 사용자 정보 탐색시 userId 이용
            Long userId = Long.valueOf(username);
            user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        } catch (NumberFormatException e) {
            // 로그인 과정 중 사용자 정보 탐색시 email 이용
            user = userRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        }

        return new UserDetailsImpl(user);
    }
}
