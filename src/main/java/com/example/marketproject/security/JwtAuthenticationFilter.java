package com.example.marketproject.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {  // OncePerRequestFilter: 요청당 한 번만 실행 보장

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * JWT 필터를 적용하지 않을 경로 지정
     * /api/auth/** 경로는 토큰 검증 없이 통과
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/");  // /api/auth/** 제외
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Request Header에서 JWT 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰 유효성 검증
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 블랙리스트 체크
            String blacklisted = redisTemplate.opsForValue().get("blacklist:" + token);
            if (blacklisted == null) {

                // 3. 토큰에서 사용자 정보 추출
                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                // 4. SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }

        // 5. 다음 필터로 전달
        filterChain.doFilter(request, response);
    }
    /**
     * Request Header에서 토큰 정보 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer " 제거
        }

        return null;

    }
}
