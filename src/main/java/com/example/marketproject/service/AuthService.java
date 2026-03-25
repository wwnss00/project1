package com.example.marketproject.service;


import com.example.marketproject.domain.entity.Role;
import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.request.LoginRequest;
import com.example.marketproject.dto.request.SignupRequest;
import com.example.marketproject.exception.AuthenticationFailedException;
import com.example.marketproject.repository.UserRepository;
import com.example.marketproject.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    // Refresh Token 만료시간 (2주)
    private static final long REFRESH_TOKEN_EXPIRE = 60 * 60 * 24 * 14L;

    @Transactional
    public Long signup(SignupRequest request) {
        //중복 체크
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 생성
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .loginId(request.getLoginId())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }
    public User authenticate(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByLoginId(request.getLoginId())
                    .orElseThrow(() -> new AuthenticationFailedException("아이디 또는 비밀번호가 일치하지 않습니다"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new AuthenticationFailedException("아이디 또는 비밀번호가 일치하지 않습니다");
        }

        return user;
        }

    // Refresh Token Redis 저장
    public void saveRefreshToken(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(
                "refreshToken:" + userId,   // key
                refreshToken,               // value
                REFRESH_TOKEN_EXPIRE,       // 만료시간
                TimeUnit.SECONDS            // 시간 단위
        );
    }

    // Refresh Token 조회
    public String getRefreshToken(Long userId) {
        return redisTemplate.opsForValue().get("refreshToken:" + userId);
    }

    // Refresh Token 삭제 (로그아웃)
    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete("refreshToken:" + userId);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
    }
    }
