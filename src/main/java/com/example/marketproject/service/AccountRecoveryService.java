package com.example.marketproject.service;


import com.example.marketproject.domain.entity.User;
import com.example.marketproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountRecoveryService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // ========================
    // 아이디 찾기
    // ========================

    // 1단계: 이름 + 이메일로 유저 확인 후 인증코드 발송
    public void sendCodeForLoginId(String name, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록된 이메일이 없습니다"));

        // 소셜 로그인 유저 차단
        if (user.getProvider() != null) {
            throw new IllegalArgumentException(
                    user.getProvider() + " 소셜 로그인 계정입니다. 소셜 로그인을 이용해주세요."
            );
        }

        // 이름 일치 확인
        if (!user.getName().equals(name)) {
            throw new IllegalArgumentException("이름이 일치하지 않습니다");
        }

        emailService.sendVerificationCode(email);
    }

    // 2단계: 인증코드 확인 후 아이디 반환
    public String verifyCodeAndGetLoginId(String email, String code) {

        if (!emailService.verifyCode(email, code)) {
            throw new IllegalArgumentException("인증코드가 일치하지 않습니다");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록된 이메일이 없습니다"));

        return user.getLoginId();
    }

    // ========================
    // 비밀번호 재설정
    // ========================

    // 1단계: 아이디 + 이메일로 유저 확인 후 인증코드 발송
    public void sendCodeForPassword(String loginId, String email) {

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("등록된 아이디가 없습니다"));

        // 소셜 로그인 유저 차단
        if (user.getProvider() != null) {
            throw new IllegalArgumentException(
                    user.getProvider() + " 소셜 로그인 계정입니다. 소셜 로그인을 이용해주세요."
            );
        }

        // 이메일 일치 확인
        if (!user.getEmail().equals(email)) {
            throw new IllegalArgumentException("이메일이 일치하지 않습니다");
        }

        emailService.sendVerificationCode(email);
    }

    // 2단계: 인증코드 확인
    // 인증 성공 여부를 Redis에 저장해서 3단계에서 검증
    public void verifyCodeForPassword(String email, String code) {

        if (!emailService.verifyCode(email, code)) {
            throw new IllegalArgumentException("인증코드가 일치하지 않습니다");
        }

        // 인증 완료 표시 Redis에 저장 (10분)
        emailService.savePasswordResetVerified(email);
    }

    // 3단계: 새 비밀번호로 변경
    @Transactional
    public void resetPassword(String email, String newPassword) {

        // 인증 완료 여부 확인
        if (!emailService.isPasswordResetVerified(email)) {
            throw new IllegalArgumentException("인증이 완료되지 않았습니다");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록된 이메일이 없습니다"));

        user.updatePassword(passwordEncoder.encode(newPassword));

        // 인증 완료 표시 삭제
        emailService.deletePasswordResetVerified(email);
    }
}
