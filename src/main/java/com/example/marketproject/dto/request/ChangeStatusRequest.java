package com.example.marketproject.dto.request;

import com.example.marketproject.domain.entity.PostStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusRequest {

    @NotNull(message = "상태를 입력해주세요")
    private PostStatus status;
}
