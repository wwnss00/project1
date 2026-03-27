package com.example.marketproject.domain.entity;

import com.example.marketproject.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50 ) //nullable = false
    private String name;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 50, nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(length = 50, nullable = false, unique = true)
    private String nickname;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String address;

    @Column(length = 500)
    private String profileImageUrl;

    @Column
    private String provider; // "google", "kakao" 등 / 일반 로그인이면 null

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isBanned = false;  // 정지 여부

    @Column
    private LocalDateTime bannedUntil;  // 정지 해제 시간 (null이면 영구정지)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateProfile(String nickname, String phone, String address) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (phone != null) {
            this.phone = phone;
        }
        if (address != null) {
            this.address = address;
        }
    }
    public void withdraw() {
        this.isDeleted = true;
    }

    public void ban(LocalDateTime bannedUntil) {
        this.isBanned = true;
        this.bannedUntil = bannedUntil;  // null이면 영구정지
    }

    public void unban() {
        this.isBanned = false;
        this.bannedUntil = null;
    }


}
