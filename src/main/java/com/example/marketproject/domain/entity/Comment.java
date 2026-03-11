package com.example.marketproject.domain.entity;

import com.example.marketproject.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<Comment> children = new ArrayList<>();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 댓글 수정
    public void update (String content) {
        this.content = content;
    }

    // 작성자 확인
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

    // 대댓글 여부 확인
    public boolean isReply() {
        return this.parent != null;
    }

    // 삭제 가능 여부 확인
    public boolean hasActiveChildren() {
        return this.children.stream().anyMatch(c -> !c.isDeleted());
    }
}
