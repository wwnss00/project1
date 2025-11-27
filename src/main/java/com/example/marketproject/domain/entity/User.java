package com.example.marketproject.domain.entity;

import com.example.marketproject.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
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

}
