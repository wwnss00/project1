package com.example.marketproject.service;

import com.example.marketproject.domain.entity.Role;
import com.example.marketproject.domain.entity.User;
import com.example.marketproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2UserService extends  DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 유저 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 소셜 서비스에서 준 정보에서 이메일, 이름 등 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 어떤 소셜 서비스인지 확인
        String provider = userRequest.getClientRegistration().getRegistrationId();

        String email;
        String nickname;

        if (provider.equals("google")) {
            // 구글은 attributes에서 바로 꺼내기
            email = (String) attributes.get("email");
            nickname = (String) attributes.get("name");

        } else if (provider.equals("naver")) {
            // 네이버는 response 키 안에 유저 정보가 있음
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");

            email = (String) response.get("email");
            nickname = (String) response.get("nickname");

        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + provider);
        }

        // DB에 있으면 조회, 없으면 자동 회원가입
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .loginId(email)
                                .nickname(nickname)
                                .password("OAUTH2_USER")
                                .provider(provider)
                                .role(Role.USER)
                                .build()
                ));

          return oAuth2User;
    }
}
