package com.example.marketproject.dto.response;

import com.example.marketproject.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminUserResponse {
    private Long id;
    private String loginId;
    private String nickname;
    private String email;
    private String role;
    private boolean deleted;
    private boolean banned;
    private LocalDateTime bannedUntil;

    public static AdminUserResponse from(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .role(user.getRole().name())
                .deleted(user.isDeleted())
                .banned(user.isBanned())
                .bannedUntil(user.getBannedUntil())
                .build();
    }
}
