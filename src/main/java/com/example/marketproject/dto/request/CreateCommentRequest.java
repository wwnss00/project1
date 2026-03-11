package com.example.marketproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentRequest {

    @NotBlank(message = "댓글을 입력해주세요")
    private String content;

    private Long parentId; // null이면 일반 댓글, 값 있으면 대댓글
}
