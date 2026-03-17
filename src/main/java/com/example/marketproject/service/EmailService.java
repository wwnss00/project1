package com.example.marketproject.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // 인증코드 임시 저장 (이메일 → 인증코드)
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    /**
     * 인증코드 이메일 발송
     */
    public void sendVerificationCode(String email) {
        // 6자리 인증코드 생성
        String code = generateCode();

        // 인증코드 저장
        verificationCodes.put(email, code);

        // 이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[마켓] 인증코드 안내");
        message.setText("인증코드: " + code + "\n\n5분 내에 입력해주세요.");

        mailSender.send(message);
        log.info("인증코드 발송 완료: {}", email);
    }

    /**
     * 인증코드 검증
     */
    public boolean verifyCode(String email, String code) {
        String savedCode = verificationCodes.get(email);
        if (savedCode != null && savedCode.equals(code)) {
            verificationCodes.remove(email); // 인증 완료 후 삭제
            return true;
        }
        return false;
    }

    /**
     * 6자리 랜덤 인증코드 생성
     */
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
