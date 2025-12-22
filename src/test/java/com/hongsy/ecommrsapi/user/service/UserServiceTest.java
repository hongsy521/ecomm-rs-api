package com.hongsy.ecommrsapi.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hongsy.ecommrsapi.user.dto.LoginRequestDto;
import com.hongsy.ecommrsapi.user.dto.SignupRequestDto;
import com.hongsy.ecommrsapi.user.entity.Gender;
import com.hongsy.ecommrsapi.user.entity.Role;
import com.hongsy.ecommrsapi.user.entity.User;
import com.hongsy.ecommrsapi.user.repository.UserRepository;
import com.hongsy.ecommrsapi.util.UserDetailsImpl;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.jwt.JwtTokenProvider;
import com.hongsy.ecommrsapi.util.jwt.RefreshToken;
import com.hongsy.ecommrsapi.util.jwt.RefreshTokenRedisRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("T1-(1). 회원가입 성공 테스트 - 유효한 정보가 입력되면 암호화 및 타입 변환 후 저장한다.")
    void signup_Success() {
        String encryptedPassword = "encryptedPassword";

        SignupRequestDto requestDto = SignupRequestDto.builder()
            .email("test@test.com")
            .password("password1234")
            .name("tester")
            .age(30)
            .gender("여성")
            .phoneNumber("010-1234-5678")
            .address("경기도 수원시")
            .roles(Set.of("판매자", "구매자"))
            .build();

        given(userRepository.findByEmail(eq(requestDto.getEmail()))).willReturn(Optional.empty());
        given(passwordEncoder.encode(any(String.class))).willReturn(encryptedPassword);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        userService.signup(requestDto);

        verify(userRepository, times(1)).findByEmail(eq(requestDto.getEmail()));
        verify(passwordEncoder, times(1)).encode(any(String.class));
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getEmail()).isEqualTo(requestDto.getEmail());
        assertThat(savedUser.getPassword()).isEqualTo(encryptedPassword);
        assertThat(savedUser.getGender()).isEqualTo(Gender.Female);
        assertThat(savedUser.getRoles()).containsExactlyInAnyOrder(Role.ROLE_SELLER,
            Role.ROLE_BUYER);
    }

    @Test
    @DisplayName("T1-(2). 회원가입 실패 테스트 - 이미 존재하는 이메일인 경우 EXISTING_USER 예외를 던진다.")
    void signup_Fail_ExistingUser() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .email("test@test.com")
            .build();

        User existingUser = User.builder()
            .email("test@test.com")
            .build();

        given(userRepository.findByEmail(eq(requestDto.getEmail()))).willReturn(
            Optional.of(existingUser));

        CustomException exception = assertThrows(CustomException.class,()->userService.signup(requestDto));

        assertThat(exception.getMessage()).isEqualTo("이미 가입된 사용자 입니다.");

        verify(passwordEncoder,never()).encode(any(String.class));
        verify(userRepository,never()).save(any(User.class));

    }

    @Test
    @DisplayName("T2-(1). 로그인 성공 테스트 - 유효한 로그인 정보 입력시 액세스토큰을 전달한다.")
    void login_Success(){

        LoginRequestDto requestDto = LoginRequestDto.builder()
            .email("test@test.com")
            .password("password1234")
            .build();

        Authentication mockAuthentication = mock(Authentication.class);
        UserDetailsImpl mockUserDetails = mock(UserDetailsImpl.class);

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .willReturn(mockAuthentication);
        given(mockAuthentication.getPrincipal()).willReturn(mockUserDetails);
        given(mockUserDetails.getId()).willReturn(1L);

        doReturn(List.of(
            new SimpleGrantedAuthority("ROLE_SELLER"),
            new SimpleGrantedAuthority("ROLE_BUYER")
        )).when(mockUserDetails).getAuthorities();

        String accessToken = "mockAccessToken";
        String refreshTokenString = "mockRefreshToken";
        String jti = "mockJti";

        given(jwtTokenProvider.createAccessToken(anyLong(), anyList())).willReturn(accessToken);
        given(jwtTokenProvider.createRefreshToken(anyLong())).willReturn(refreshTokenString);
        given(jwtTokenProvider.getJti(refreshTokenString)).willReturn(jti);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        String result = userService.login(requestDto, response);

        verify(response).addCookie(cookieCaptor.capture());
        Cookie savedCookie = cookieCaptor.getValue();

        assertThat(savedCookie.getName()).isEqualTo("refreshToken");
        assertThat(savedCookie.isHttpOnly()).isEqualTo(true);
        assertThat(savedCookie.getMaxAge()).isEqualTo(1209600);
        assertThat(savedCookie.getPath()).isEqualTo("/");

        assertThat(result).isEqualTo("Bearer " + accessToken);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(refreshTokenRedisRepository).save(any(RefreshToken.class));
        verify(response).addCookie(any(Cookie.class));

        verify(jwtTokenProvider).createAccessToken(eq(1L), anyList());
        verify(jwtTokenProvider).createRefreshToken(eq(1L));
    }

    @Test
    @DisplayName("T2-(2). 로그인 실패 테스트 - 잘못된 이메일/비밀번호 경우 AuthenticationManager 예외 처리")
    void login_Fail_InvalidAuthentication(){
        LoginRequestDto requestDto = LoginRequestDto.builder()
            .email("test@test.com")
            .password("wrongPassword")
            .build();

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .willThrow(new BadCredentialsException("자격 증명에 실패하였습니다."));

        assertThatThrownBy(() -> userService.login(requestDto, response))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessageContaining("자격 증명에 실패하였습니다.");

        verify(jwtTokenProvider, never()).createAccessToken(anyLong(), anyList());
        verify(refreshTokenRedisRepository, never()).save(any());
        verify(response, never()).addCookie(any());
    }

    @Test
    @DisplayName("T2-(3). 로그인 실패 테스트 - 존재하지 않는 사용자일 경우 예외 처리")
    void login_Fail_NonExistingUser(){
        LoginRequestDto requestDto = LoginRequestDto.builder()
            .email("test@test.com")
            .password("wrongPassword")
            .build();

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .willThrow(new InternalAuthenticationServiceException("존재하지 않는 사용자입니다."));

        assertThatThrownBy(() -> userService.login(requestDto, response))
            .isInstanceOf(InternalAuthenticationServiceException.class)
            .hasMessageContaining("존재하지 않는 사용자입니다.");

        verify(jwtTokenProvider, never()).createAccessToken(anyLong(), anyList());
        verify(refreshTokenRedisRepository, never()).save(any());
        verify(response, never()).addCookie(any());
    }

}