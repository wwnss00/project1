package com.example.marketproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindLoginIdResponse {
    private String loginId;
    private String message;
}
