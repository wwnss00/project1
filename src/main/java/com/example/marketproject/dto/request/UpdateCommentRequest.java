package com.example.marketproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCommentRequest {

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;
}
