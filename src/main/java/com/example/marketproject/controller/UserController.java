package com.example.marketproject.controller;

import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.request.UpdatePasswordRequest;
import com.example.marketproject.dto.request.UpdateProfileRequest;
import com.example.marketproject.dto.response.MessageResponse;
import com.example.marketproject.dto.response.UserResponse;
import com.example.marketproject.security.CustomUserDetails;
import com.example.marketproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PatchMapping("/me/profile-image")
    public ResponseEntity<Void> updateProfileImage(
            @RequestParam MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {

        userService.updateProfileImage(userDetails.getUserId(), image);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me")
    public ResponseEntity<MessageResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        userService.updateProfile(userDetails.getUserId(), request);
        return ResponseEntity.ok(new MessageResponse("회원정보가 수정되었습니다"));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<MessageResponse> updatePassword(
            @RequestBody UpdatePasswordRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        userService.updatePassword(userDetails.getUserId(), request);
        return ResponseEntity.ok(new MessageResponse("비밀번호가 변경되었습니다"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getUserId());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request) {  // request 추가

        String token = resolveToken(request);  // 토큰 추출
        userService.withdraw(userDetails.getUserId(), token);  // 토큰 전달
        return ResponseEntity.noContent().build();
    }

    // 토큰 추출 메서드 추가
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}


