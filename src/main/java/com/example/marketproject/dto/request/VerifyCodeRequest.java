package com.example.marketproject.dto.request;

import lombok.Getter;

@Getter
public class VerifyCodeRequest {
    private String email;
    private String code;
}
