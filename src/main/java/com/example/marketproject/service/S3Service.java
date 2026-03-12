package com.example.marketproject.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("prod")
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    // 파일 업로드
    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        validateImageFile(originalFilename);

        String storedFilename = UUID.randomUUID() + "_" + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3.putObject(bucket, storedFilename, file.getInputStream(), metadata);

        return amazonS3.getUrl(bucket, storedFilename).toString();
    }

    // 파일 삭제
    public void deleteFile(String fileUrl) {
        String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        amazonS3.deleteObject(bucket, filename);
    }

    // 확장자 검증
    private void validateImageFile(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!extension.matches("jpg|jpeg|png|gif|webp")) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다");
        }
    }
}
