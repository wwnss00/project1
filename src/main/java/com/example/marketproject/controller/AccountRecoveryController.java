package com.example.marketproject.controller;

import com.example.marketproject.dto.request.FindLoginIdRequest;
import com.example.marketproject.dto.request.NewPasswordRequest;
import com.example.marketproject.dto.request.ResetPasswordRequest;
import com.example.marketproject.dto.request.VerifyCodeRequest;
import com.example.marketproject.dto.response.FindLoginIdResponse;
import com.example.marketproject.dto.response.MessageResponse;
import com.example.marketproject.service.AccountRecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AccountRecoveryController {

    private final AccountRecoveryService accountRecoveryService;

    // ========================
    // 아이디 찾기
    // ========================

    // 1단계: 인증코드 발송
    @PostMapping("/find-loginid/send-code")
    public ResponseEntity<MessageResponse> sendCodeForLoginId(
            @RequestBody FindLoginIdRequest request) {
        accountRecoveryService.sendCodeForLoginId(request.getName(), request.getEmail());
        return ResponseEntity.ok(new MessageResponse("인증코드가 발송되었습니다"));
    }

    // 2단계: 인증코드 확인 후 아이디 반환
    @PostMapping("/find-loginid/verify")
    public ResponseEntity<FindLoginIdResponse> verifyCodeAndGetLoginId(
            @RequestBody VerifyCodeRequest request) {
        String loginId = accountRecoveryService.verifyCodeAndGetLoginId(
                request.getEmail(),
                request.getCode()
        );
        return ResponseEntity.ok(new FindLoginIdResponse(loginId, "아이디를 찾았습니다"));
    }

    // ========================
    // 비밀번호 재설정
    // ========================

    // 1단계: 인증코드 발송
    @PostMapping("/reset-password/send-code")
    public ResponseEntity<MessageResponse> sendCodeForPassword(
            @RequestBody ResetPasswordRequest request) {
        accountRecoveryService.sendCodeForPassword(request.getLoginId(), request.getEmail());
        return ResponseEntity.ok(new MessageResponse("인증코드가 발송되었습니다"));
    }

    // 2단계: 인증코드 확인
    @PostMapping("/reset-password/verify")
    public ResponseEntity<MessageResponse> verifyCodeForPassword(
            @RequestBody VerifyCodeRequest request) {
        accountRecoveryService.verifyCodeForPassword(request.getEmail(), request.getCode());
        return ResponseEntity.ok(new MessageResponse("인증이 완료되었습니다"));
    }

    // 3단계: 새 비밀번호로 변경
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<MessageResponse> resetPassword(
            @RequestBody NewPasswordRequest request) {
        accountRecoveryService.resetPassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("비밀번호가 변경되었습니다"));
    }
}
