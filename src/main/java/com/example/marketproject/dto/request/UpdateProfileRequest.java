package com.example.marketproject.dto.request;

import lombok.Getter;

@Getter
public class UpdateProfileRequest {
    private String nickname;
    private String phone;
    private String address;
}
