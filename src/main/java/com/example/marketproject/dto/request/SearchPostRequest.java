package com.example.marketproject.dto.request;

import com.example.marketproject.domain.entity.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchPostRequest {

    private String keyword;     // 제목/내용 검색
    private Integer minPrice;   // 최소 가격
    private Integer maxPrice;   // 최대 가격
    private String location;    // 지역
    private PostStatus status;  // 상태
}
