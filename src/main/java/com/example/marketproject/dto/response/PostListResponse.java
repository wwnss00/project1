package com.example.marketproject.dto.response;

import com.example.marketproject.domain.entity.Post;
import com.example.marketproject.domain.entity.PostStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostListResponse {

    private Long id;
    private String title;
    private Integer price;
    private String location;
    private PostStatus status;
    private Integer viewCount;
    private String writerNickname;
    private String thumbnailUrl;
    private LocalDateTime createdAt;

    public static PostListResponse from(Post post) {

        String thumbnailUrl = post.getImages().stream()
                .filter(img -> img.getIsThumbnail())
                .findFirst()
                .map(img -> "/api/images/" + img.getStoredFilename())
                .orElse(null);

        return PostListResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .price(post.getPrice())
                .location(post.getLocation())
                .status(post.getStatus())
                .viewCount(post.getViewCount())
                .writerNickname(post.getUser().getNickname())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
