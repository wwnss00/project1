package com.example.marketproject.dto.response;

import com.example.marketproject.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String loginId;
    private String nickname;
    private String email;
    private String phone;
    private String address;
    private String profileImageUrl;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
