package com.example.marketproject.controller;

import com.example.marketproject.domain.entity.Role;
import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.request.SignupRequest;
import com.example.marketproject.repository.UserRepository;
import com.example.marketproject.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("local")
public class AuthControllerTest {

    @Autowired
    private  MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입_성공_테스트")
    void 회원가입_성공() throws Exception {
        //given
        SignupRequest request = new SignupRequest().builder()
                .name("김준성")
                .email("jkl2085@naver.com")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("준준준")
                .phone("010-2295-4328")
                .build();

        //when
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());

        //then
        //DB검증: 실제로 저장되었는지 확인
        User savedUser = userRepository.findByLoginId("jkl2085")
                .orElseThrow(() -> new AssertionError("회원가입 실패: DB에 저장되지 않음"));

        // 저장된 데이터 검증
        assertThat(savedUser.getName()).isEqualTo("김준성");
        assertThat(savedUser.getEmail()).isEqualTo("jkl2085@naver.com");
        assertThat(savedUser.getLoginId()).isEqualTo("jkl2085");
        assertThat(savedUser.getPassword()).isNotEqualTo("!wanns2085");
        assertThat(savedUser.getNickname()).isEqualTo("준준준");
        assertThat(savedUser.getPhone()).isEqualTo("010-2295-4328");
    }

    @Test
    @DisplayName("회원가입_실패_중복_이메일")
    void signup_실패_중복_이메일() throws Exception {

        //given
        SignupRequest firstrequest = new SignupRequest().builder()
                .name("김준성")
                .email("jkl2085@naver.com")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("준준준")
                .phone("010-2295-4328")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstrequest)))
                .andExpect(status().isOk());

        //when
        SignupRequest secondrequest = new SignupRequest().builder()
                .name("김준승")
                .email("jkl2085@naver.com")
                .loginId("jka2085")
                .password("!wenns2085")
                .nickname("준쥰준")
                .phone("010-2225-4328")
                .build();

        //then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondrequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 사용중인 이메일입니다."))
                .andDo(print());
    }
}
