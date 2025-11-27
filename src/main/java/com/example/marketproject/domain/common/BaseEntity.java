package com.example.marketproject.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;

@MappedSuperclass  // JPA 엔티티 클래스들이 이 클래스를 상속하면 필드들을 컬럼으로 인식
@EntityListeners(AuditingEntityListener.class)  // Auditing 기능 포함
@Getter
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false, nullable = false) //수정 불가능
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티 수정 시 자동으로 시간 갱신
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
