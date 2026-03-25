package com.example.marketproject.repository;


import com.example.marketproject.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // JPA 관련 Bean만 로드, H2 인메모리 DB 사용
@EnableJpaAuditing
class UserRepositoryTest {

    @Autowired  // Mock 아님! 진짜 Repository 주입
    private UserRepository userRepository;


    // TODO 1. loginId로 유저 조회
    @Test
    @DisplayName("loginId로 유저 조회 성공")
    void findByLoginId_success() {

        // given - H2 DB에 실제로 저장
        User user = User.builder()
                .name("김준성")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("김준성")
                .email("jkl2085@naver.com")
                .phone("01022954328")
                .build();
        userRepository.save(user);

        // when - 실제 쿼리 실행
        Optional<User> result = userRepository.findByLoginId(user.getLoginId());

        // then - 결과 검증
        assertThat(result).isPresent();  // Optional에 값이 있는지
        assertThat(result.get().getLoginId()).isEqualTo("jkl2085");  // loginId가 맞는지
    }


    // TODO 2. 이메일 중복 체크
    @Test
    @DisplayName("이메일 중복 체크 - 존재하는 경우 true 리턴")
    void existsByEmail_true() {

        // given
        User user = User.builder()
                .name("김준성")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("김준성")
                .email("jkl2085@naver.com")
                .phone("01022954328")
                .build();
        userRepository.save(user);

        // when
        Boolean result = userRepository.existsByEmail(user.getEmail());

        // then
        assertThat(result).isTrue();
    }
}