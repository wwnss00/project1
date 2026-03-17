package com.example.marketproject.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    // 인증코드 만료시간 5분
    private static final long CODE_EXPIRE = 60 * 5L;

    /**
     * 인증코드 이메일 발송
     */
    public void sendVerificationCode(String email) {
        // 6자리 인증코드 생성
        String code = generateCode();

        // Redis에 저장 (key: "emailCode:이메일", TTL: 5분)
        redisTemplate.opsForValue().set(
                "emailCode:" + email,
                code,
                CODE_EXPIRE,
                TimeUnit.SECONDS
        );

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
        String savedCode = redisTemplate.opsForValue().get("emailCode:" + email);

        if (savedCode != null && savedCode.equals(code)) {
            // 인증 완료 후 Redis에서 삭제
            redisTemplate.delete("emailCode:" + email);
            return true;
        }
        return false;
    }

    // 인증 완료 표시 저장 (10분)
    public void savePasswordResetVerified(String email) {
        redisTemplate.opsForValue().set(
                "passwordReset:" + email,
                "verified",
                60 * 10L,
                TimeUnit.SECONDS
        );
    }

    // 인증 완료 여부 확인
    public boolean isPasswordResetVerified(String email) {
        return redisTemplate.hasKey("passwordReset:" + email);
    }

    // 인증 완료 표시 삭제
    public void deletePasswordResetVerified(String email) {
        redisTemplate.delete("passwordReset:" + email);
    }

    /**
     * 6자리 랜덤 인증코드 생성
     */
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
