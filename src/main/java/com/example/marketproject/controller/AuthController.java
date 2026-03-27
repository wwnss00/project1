package com.example.marketproject.controller;


import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.request.LoginRequest;
import com.example.marketproject.dto.request.SignupRequest;
import com.example.marketproject.dto.response.LoginResponse;
import com.example.marketproject.dto.response.SignupResponse;
import com.example.marketproject.dto.response.TokenResponse;
import com.example.marketproject.security.JwtTokenProvider;
import com.example.marketproject.service.AuthService;
import com.example.marketproject.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        Long userId = authService.signup(request);

        SignupResponse response = new SignupResponse(userId, "회원가입이 완료되었습니다");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
                @Valid @RequestBody LoginRequest request,
                HttpServletResponse response)
    {
            // 인증
            User user = authService.authenticate(request);

            // Access Token 생성
            String accessToken = jwtTokenProvider.createAccessToken(
                    user.getId(),
                    user.getLoginId(),
                    user.getRole().name()
            );

            // Refresh Token 생성
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // Redis에 Refresh Token 저장 추가
        authService.saveRefreshToken(user.getId(), refreshToken);

            // Refresh Token을 HttpOnly Cookie에 저장
            Cookie cookie = cookieUtil.createRefreshTokenCookie(refreshToken);
            response.addCookie(cookie);

            LoginResponse loginResponse = new LoginResponse(accessToken);
            return ResponseEntity.ok(loginResponse);
        }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        // Redis에서 Refresh Token 삭제
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            Long userId = jwtTokenProvider.getUserId(refreshToken);
            authService.deleteRefreshToken(userId);
        }

        // Cookie 삭제
        Cookie cookie = cookieUtil.deleteRefreshTokenCookie();
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

        @PostMapping("/refresh")
        public ResponseEntity<TokenResponse> refresh(
                @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
            // Refresh Token 확인
            if (refreshToken == null) {
                throw new IllegalArgumentException("Refresh Token이 없습니다");
            }

            // Refresh Token 검증
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다");
            }

            // userId 추출
            Long userId = jwtTokenProvider.getUserId(refreshToken);

            // Redis에 저장된 토큰과 비교
            String savedToken = authService.getRefreshToken(userId);
            if (savedToken == null || !savedToken.equals(refreshToken)) {
                throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다");
            }

            // DB에서 사용자 조회 (최신 정보)
            User user = authService.getUserById(userId);

            // 새 Access Token 발급
            String newAccessToken = jwtTokenProvider.createAccessToken(
                    user.getId(),
                    user.getLoginId(),
                    user.getRole().name()
            );

            TokenResponse tokenResponse = new TokenResponse(newAccessToken);
            return ResponseEntity.ok(tokenResponse);
        }

    @GetMapping("/check-loginid")
    public ResponseEntity<Map<String, Boolean>> checkLoginId(@RequestParam String loginId) {
        boolean available = authService.isLoginIdAvailable(loginId);
        return ResponseEntity.ok(Map.of("available", available));
    }

        
    }
