package com.example.marketproject.service;


import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.request.UpdatePasswordRequest;
import com.example.marketproject.dto.request.UpdateProfileRequest;
import com.example.marketproject.exception.UserNotFoundException;
import com.example.marketproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    @Autowired(required = false)
    private S3Service s3Service;
    private final FileStorageService fileStorageService; // 로컬
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        user.updateProfile(
                request.getNickname(),
                request.getPhone(),
                request.getAddress()
        );
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 소셜 로그인 유저 차단
        if (user.getProvider() != null) {
            throw new IllegalArgumentException("소셜 로그인 계정은 비밀번호를 변경할 수 없습니다.");
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void updateProfileImage(Long userId, MultipartFile image) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        String imageUrl;
        if (s3Service != null) {
            imageUrl = s3Service.uploadFile(image);
        } else {
            imageUrl = "/uploads/images/" + fileStorageService.storeFile(image);
        }
        user.updateProfileImage(imageUrl);
        // @Transactional + dirty checking → save() 불필요
    }
}
