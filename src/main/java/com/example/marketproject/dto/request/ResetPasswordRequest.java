package com.example.marketproject.dto.request;

import lombok.Getter;

@Getter
public class ResetPasswordRequest {
    private String loginId;
    private String email;
}
