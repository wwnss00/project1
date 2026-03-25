package com.example.marketproject.dto.response;

import com.example.marketproject.domain.entity.Post;
import com.example.marketproject.domain.entity.PostImage;
import com.example.marketproject.domain.entity.PostStatus;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Builder
public class PostResponse {

    // 게시글 기본 정보
    private Long id;
    private String title;
    private String content;
    private Integer price;
    private String location;
    private List<ImageInfo> images;

    //게시글 상태 정보
    private PostStatus status;
    private Integer viewCount;

    private WriterInfo writer;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @Getter
    @Builder
    public static class WriterInfo {
        private Long id;
        private String nickname;
    }

    @Getter
    @Builder
    public static class ImageInfo {
        private Long id;
        private String imageUrl;
        private Integer imageOrder;
        private Boolean isThumbnail;

        public static ImageInfo from(PostImage postImage) {
            return ImageInfo.builder()
                    .id(postImage.getId())
                    .imageUrl(postImage.getFilePath())  // S3 전체 URL 사용
                    .imageOrder(postImage.getImageOrder())
                    .isThumbnail(postImage.getIsThumbnail())
                    .build();
        }
    }

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .price(post.getPrice())
                .location(post.getLocation())
                .status(post.getStatus())
                .viewCount(post.getViewCount())
                .writer(WriterInfo.builder()
                        .id(post.getUser().getId())
                        .nickname(post.getUser().getNickname())
                        .build())
                .images(post.getImages().stream()  // ← 추가!
                        .map(ImageInfo::from)
                        .collect(Collectors.toList()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();

    }
}
