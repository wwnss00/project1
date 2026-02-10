package com.example.marketproject.dto.response;

import com.example.marketproject.domain.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long id;
    private String content;
    private WriterInfo writer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class WriterInfo {
        private Long id;
        private String nickname;
    }

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .writer(WriterInfo.builder()
                        .id(comment.getUser().getId())
                        .nickname(comment.getUser().getNickname())
                        .build())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

}
