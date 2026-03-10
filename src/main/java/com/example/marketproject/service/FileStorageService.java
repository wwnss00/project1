package com.example.marketproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService  {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 파일 저장
    public String storeFile(MultipartFile file) throws IOException {
        // 1. 원본 파일명
        String originalFilename = file.getOriginalFilename();

        // 2. 확장자 검증
        validateImageFile(originalFilename);

        // 3. UUID 생성
        String storedFilename = UUID.randomUUID() + "_" + originalFilename;

        // 4. 저장 경로
        Path uploadPath = Paths.get(uploadDir);

        // 5. 디렉토리 없으면 생성
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 6. 파일 저장
        Path filePath = uploadPath.resolve(storedFilename);
        file.transferTo(filePath.toFile());

        return storedFilename;
    }

    // 확장자 검증
        private void validateImageFile(String filename) {
            String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

            if (!extension.matches("jpg|jpeg|png|gif|webp")) {
                throw new IllegalArgumentException("지원하지 않는 파일 형식입니다");
            }
    }

    // 파일 경로 반환 (ImageController용)
    public Path getFilePath(String filename) {
        return Paths.get(uploadDir).resolve(filename);
    }
}
