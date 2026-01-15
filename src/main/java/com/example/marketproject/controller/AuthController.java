package com.example.marketproject.controller;


import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.request.LoginRequest;
import com.example.marketproject.dto.request.SignupRequest;
import com.example.marketproject.dto.response.LoginResponse;
import com.example.marketproject.dto.response.SignupResponse;
import com.example.marketproject.dto.response.TokenResponse;
import com.example.marketproject.security.JwtTokenProvider;
import com.example.marketproject.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        Long userId = authService.signup(request);

        SignupResponse response = new SignupResponse(userId, "회원가입이 완료되었습니다");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
                @Valid @RequestBody LoginRequest request,
                HttpServletResponse response,
                @Value("${cookie.secure}") boolean cookieSecure)
    {
            // 인증
            User user = authService.authenticate(request);

            // Access Token 생성
            String accessToken = jwtTokenProvider.createAccessToken(
                    user.getId(),
                    user.getRole().name()
            );

            // Refresh Token 생성
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

            // Refresh Token을 HttpOnly Cookie에 저장
            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(cookieSecure);  // 환경별 설정
            cookie.setPath("/");
            cookie.setMaxAge(2 * 60 * 60);  // 2시간 (초 단위)
            response.addCookie(cookie);

            LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken);
            return ResponseEntity.ok(loginResponse);
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

            // DB에서 사용자 조회 (최신 정보)
            User user = authService.getUserById(userId);

            // 새 Access Token 발급
            String newAccessToken = jwtTokenProvider.createAccessToken(
                    user.getId(),
                    user.getRole().name()
            );

            TokenResponse tokenResponse = new TokenResponse(newAccessToken);
            return ResponseEntity.ok(tokenResponse);
        }

        
    }
