package com.hongsy.ecommrsapi.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hongsy.ecommrsapi.util.RepositorySliceTest;
import com.hongsy.ecommrsapi.user.entity.Role;
import com.hongsy.ecommrsapi.user.entity.User;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DisplayName("UserRepository 슬라이스 테스트")
class UserRepositoryTest extends RepositorySliceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("T1-(1). findByEmail 성공 테스트 - 이메일을 이용한 사용자 조회")
    void findByEmail_Success(){
        User user = User.builder()
            .email("test@test.com")
            .password("password")
            .roles(Set.of(Role.ROLE_SELLER,Role.ROLE_BUYER))
            .build();

        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("test@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("T1-(2). findByEmail 실패 테스트")
    void findByEmail_Fail(){
        Optional<User> result = userRepository.findByEmail("test@test.com");

        assertThat(result).isEmpty();
    }

}