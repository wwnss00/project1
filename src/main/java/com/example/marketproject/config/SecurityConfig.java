package com.example.marketproject.config;

import com.example.marketproject.security.JwtAuthenticationFilter;
import com.example.marketproject.security.OAuth2SuccessHandler;
import com.example.marketproject.service.OAuth2UserService;
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
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

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
                                //  원래부터 열어둔 경로 (변경 없음)
                                "/api/auth/login",
                                "/api/auth/signup",
                                "/api/auth/refresh",
                                "/login/oauth2/**",
                                "/oauth2/**",
                                "/api/auth/find-loginid/**",
                                "/api/auth/reset-password/**",
                                "/ws/chat/**",
                                "/chat-test.html",
                                "/api/posts",
                                "/api/posts/{id}",
                                "/api/auth/check-loginid",


                                // Thymeleaf 시연용으로 추가한 경로
                                // React 연결 시 아래 경로들은 다시 제거해도 됨
                                "/login", "/register",
                                "/posts", "/posts/**",
                                "/chat/rooms", "/chat/rooms/**",
                                "/find-id", "/find-password",
                                "/mypage/**",
                                "/admin/**"
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

                // OAuth2 로그인 설정 추가
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)  // 만든 서비스 연결
                        )
                        .successHandler(oAuth2SuccessHandler)    // 로그인 성공 후 처리
                )

                // JWT 필터 추가
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

}
