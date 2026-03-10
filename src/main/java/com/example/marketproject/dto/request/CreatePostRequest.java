package com.example.marketproject.dto.request;

import com.example.marketproject.domain.entity.PostStatus;
import com.example.marketproject.domain.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class CreatePostRequest {

    @NotBlank(message = "제목을 입력해주세요")
    @Size(max = 100)
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    @NotNull(message = "가격을 입력해주세요")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다")
    private Integer price;

    @NotBlank(message = "장소를 입력해주세요")
    @Size(max = 100)
    private String location;

}
