package com.example.marketproject.dto.response;

import com.example.marketproject.domain.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminPostResponse {
    private Long id;
    private String title;
    private String writerNickname;
    private String saleStatus;   // 판매상태
    private boolean deleted;     // 삭제여부

    public static AdminPostResponse from(Post post) {
        return AdminPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .writerNickname(post.getUser().getNickname())
                .saleStatus(post.getStatus().getDescription())  // "판매중", "예약중", "판매완료"
                .deleted(post.getDeletedAt() != null)
                .build();
    }
}