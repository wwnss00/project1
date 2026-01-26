package com.example.marketproject.domain.entity;


import com.example.marketproject.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer price;

    @Column(length = 100, nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PostStatus status = PostStatus.ON_SALE;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(updatable = false)
    private LocalDateTime deletedAt;

    //수정
    public void update(String title, String content, Integer price, String location, PostStatus status) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.location = location;
        this.status = status;
    }

    //조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }

    //작성자 확인
    public boolean isWriter(Long userId) {
        return this.user.getId().equals(userId);
    }

    //소프트 삭제
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }


}
