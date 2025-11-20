package com.hongsy.ecommrsapi.util;

import com.hongsy.ecommrsapi.user.entity.User;
import com.hongsy.ecommrsapi.user.repository.UserRepository;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long userId=Long.valueOf(username);

        User user = userRepository.findById(userId).orElseThrow(
            ()->  new CustomException(ErrorCode.NON_EXISTENT_USER)
        );

        return new UserDetailsImpl(user);
    }
}
