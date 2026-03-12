package com.example.marketproject.service;


import com.example.marketproject.domain.entity.User;
import com.example.marketproject.exception.UserNotFoundException;
import com.example.marketproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
