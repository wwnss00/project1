package com.example.marketproject.dto.request;

import lombok.Getter;

@Getter
public class NewPasswordRequest {
    private String email;
    private String newPassword;
}