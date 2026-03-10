package com.example.marketproject.domain.entity;


import com.example.marketproject.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private PostStatus status = PostStatus.ON_SALE;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @Column  
    private LocalDateTime deletedAt;

    //수정
    public void update(String title, String content, Integer price, String location) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (price != null) {
            this.price = price;
        }
        if (location != null) {
            this.location = location;
        }
    }

    //상태 변경
    public void changeStatus(PostStatus status) {
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

    //삭제 여부 확인
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void addImage(PostImage image) {
        images.add(image);
    }

    @Builder
    public Post(User user, String title, String content, Integer price, String location) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.price = price;
        this.location = location;
        this.status = PostStatus.ON_SALE;
        this.viewCount = 0;
        this.images = new ArrayList<>();
    }


}
