package com.example.marketproject.config;

import com.example.marketproject.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF 비활성화 (JWT 사용하므로)
                .csrf(csrf -> csrf.disable())

                // 세션 사용 안함 (Stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // URL별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/signup",
                                "/api/auth/refresh"
                        ).permitAll()

                        // H2 콘솔 접근 허용 (개발 편의)
                        .requestMatchers("/h2-console/**").permitAll()

                        // ADMIN만 접근 가능
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // swagger 경로 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // H2 콘솔 사용을 위한 설정
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )

                // JWT 필터 추가
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
