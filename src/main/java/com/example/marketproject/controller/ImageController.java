package com.example.marketproject.controller;



import com.example.marketproject.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final FileStorageService fileStorageService;

    /**
     * 이미지 조회
     * GET /api/images/{filename}
     */
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {

        // 1. 파일 경로
        Path filePath = fileStorageService.getFilePath(filename);

        // 2. Resource로 변환
        Resource resource = new UrlResource(filePath.toUri());

        // 3. 파일 존재 확인
        if (!resource.exists()) {
            throw new RuntimeException("파일을 찾을 수 없습니다: " + filename);
        }

        // 4. Content-Type 결정
        String contentType = determineContentType(filename);

        // 5. 응답
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    /**
     * Content-Type 결정
     */
    private String determineContentType(String filename) {
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }
}
