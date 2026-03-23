package com.example.marketproject.security;

import com.example.marketproject.domain.entity.User;
import com.example.marketproject.repository.UserRepository;
import com.example.marketproject.service.AuthService;
import com.example.marketproject.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CookieUtil cookieUtil;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        // 1. 유저 정보 꺼내기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // provider 확인
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        String email;

        if (registrationId.equals("naver")) {
            // 네이버는 response 안에 있음
            Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
            email = (String) naverResponse.get("email");
        } else {
            // 구글은 바로 꺼내기
            email = (String) attributes.get("email");
        }

        // 2. DB에서 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 3. JWT 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getLoginId(),
                user.getRole().name()
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // Redis에 Refresh Token 저장 추가
        authService.saveRefreshToken(user.getId(), refreshToken);

        // 4. Refresh Token 쿠키에 저장
        Cookie cookie = cookieUtil.createRefreshTokenCookie(refreshToken);
        response.addCookie(cookie);

        // 5. Access Token을 URL 파라미터로 프론트엔드에 전달
        String redirectUrl = "http://localhost:8080/posts?token=" + accessToken; // < 테스트용
        // String redirectUrl = "http://localhost:3000?accessToken=" + accessToken;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
