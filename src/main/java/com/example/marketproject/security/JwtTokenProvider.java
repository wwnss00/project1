package com.example.marketproject.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import io.jsonwebtoken.security.SignatureException;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key secretKey;
    private final long accessTokenExpire;
    private final long refreshTokenExpire;

    // 생성자: application-secret.yml의 값을 주입받음
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expire}") long accessTokenExpire,
            @Value("${jwt.refresh-token-expire}") long refreshTokenExpire
    ) {
        // Base64로 인코딩된 secret을 Key 객체로 변환
        byte[] keyBytes = Base64.getDecoder().decode(
                Base64.getEncoder().encodeToString(secret.getBytes())
        );
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpire = accessTokenExpire;
        this.refreshTokenExpire = refreshTokenExpire;
    }

    /**
     * Access Token 생성
     * @param userId 사용자 ID
     * @param role 사용자 권한
     * @return JWT 토큰 문자열
     */
    public String createAccessToken(Long userId,String loginId, String role) {

        //현재 시간
        Date now = new Date();
        //만료 시간
        Date expiration = new Date(now.getTime() + accessTokenExpire);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))     // JWT의 sub (사용자 ID)
                .claim("loginId", loginId)             // 커스텀 클레임 (로그인 id)
                .claim("role", role)                     // 커스텀 클레임 (권한)
                .setIssuedAt(now)                        // 발급시간
                .setExpiration(expiration)               // 만료시간
                .signWith(secretKey, SignatureAlgorithm.HS256)  // 서명
                .compact();                              // 문자열로 변환
    }

    /**
     * Refresh Token 생성
     * @param userId 사용자 ID
     * @return JWT 토큰 문자열
     */
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpire);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 유효성 검증
     * @param token JWT 토큰
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)  // 서버의 비밀키로 검증
                    .build()
                    .parseClaimsJws(token);    // 파싱 (여기서 검증 수행)
            return true;

        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰입니다.");
        } catch (MalformedJwtException e) {
            log.error("잘못된 형식의 토큰입니다.");
        } catch (SignatureException e) {
            log.error("서명이 일치하지 않습니다.");
        } catch (IllegalArgumentException e) {
            log.error("토큰이 비어있습니다.");
        }
        return false;
    }

    /**
     * 토큰에서 사용자 ID 추출
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // subject에서 userId 추출 (String → Long 변환)
        Long userId = Long.parseLong(claims.getSubject());

        // claim에서 loginId 추출
        String loginId = claims.get("loginId", String.class);

        // claim에서 role 추출
        String role = claims.get("role", String.class);

        CustomUserDetails userDetails = new CustomUserDetails(userId, loginId, role);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()  // CustomUserDetails가 제공
        );
    }

    /**
     * 토큰을 파싱해서 Claims 추출
     * @param token JWT 토큰
     * @return Claims (토큰의 내용물)
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();  // Payload 부분 반환
    }

    // 토큰 만료시간 추출
    public long getExpiration(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
        // 현재시간 기준 남은 만료시간 (밀리초)
    }

}
