package com.example.marketproject.service;

import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.request.LoginRequest;
import com.example.marketproject.dto.request.SignupRequest;
import com.example.marketproject.exception.AuthenticationFailedException;
import com.example.marketproject.repository.UserRepository;
import com.example.marketproject.security.JwtTokenProvider;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private StringRedisTemplate redisTemplate;

    // TODO 1. 회원가입 성공 테스트
    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {

        //given
        SignupRequest request = SignupRequest.builder()
                .name("김준성")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("팜하니")
                .email("jkl2085@naver.com")
                .phone("01022954328")
                .build();

        User user = User.builder()
                .name(request.getName())
                .loginId(request.getLoginId())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        given(userRepository.existsByLoginId("jkl2085")).willReturn(false);
        given(userRepository.existsByEmail("jkl2085@naver.com")).willReturn(false);
        given(userRepository.existsByNickname("팜하니")).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);


        //when
        Long result = authService.signup(request);

        //then
        assertThat(result).isEqualTo(user.getId());
        verify(userRepository, times(1)).save(any(User.class));


    }

    // TODO 2. 회원가입 실패 - 아이디 중복
    @Test
    @DisplayName("회원가입 실패 - 아이디 중복")
    void signup_fail_duplicateLoginId() {

        //given
        SignupRequest request = SignupRequest.builder()
                .name("김준성")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("팜하니")
                .email("jkl2085@naver.com")
                .phone("01022954328")
                .build();

        given(userRepository.existsByLoginId("jkl2085")).willReturn(true);

        //when & then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 아이디입니다.");
        verify(userRepository, never()).save(any(User.class));




    }

    // TODO 3. 로그인 성공
    @Test
    @DisplayName("로그인 성공")
    void authenticate_success() {
        // given
        User user = User.builder()
                .name("김준성")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("김준성")
                .email("jkl2085@naver.com")
                .phone("01022954328")
                .build();

        LoginRequest request = LoginRequest.builder()
                        .loginId(user.getLoginId())
                        .password(user.getPassword())
                        .build();


        given(userRepository.findByLoginId("jkl2085")).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any())).willReturn(true);

        // when
        User result = authService.authenticate(request);

        // then
        assertThat(result.getLoginId()).isEqualTo("jkl2085");


    }

    // TODO 4. 로그인 실패 - 비밀번호 불일치
    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void authenticate_fail_wrongPassword() {
        //given
        User user = User.builder()
                .name("김준성")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("김준성")
                .email("jkl2085@naver.com")
                .phone("01022954328")
                .build();

        LoginRequest request = LoginRequest.builder()
                .loginId(user.getLoginId())
                .password(user.getPassword())
                .build();

        given(userRepository.findByLoginId("jkl2085")).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.authenticate(request))
                .isInstanceOf(AuthenticationFailedException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다");



    }
}
