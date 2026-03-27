package com.example.marketproject.domain.entity;

import com.example.marketproject.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "post_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class

PostImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 파일 메타데이터 (상세)
    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String storedFilename;

    @Column(nullable = false)
    private String filePath;

    private Long fileSize;

    // 사진 순서
    @Column(name = "image_order", nullable = false)
    private Integer imageOrder;

    // 썸네일 여부
    @Column(name = "is_thumnail", nullable = false)
    private Boolean isThumbnail;

    @Builder
    public PostImage(Post post, String originalFilename, String storedFilename,
                     String filePath, Long fileSize, Integer imageOrder, Boolean isThumbnail) {
        this.post = post;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.imageOrder = imageOrder;
        this.isThumbnail = isThumbnail != null ? isThumbnail : false;
    }

    // 전체 URL 생성 (로컬/S3 대응)
    public String getImageUrl() {
        // 로컬: /uploads/images/UUID_아이폰.jpg
        // S3: https://s3.../UUID_아이폰.jpg
        return filePath + storedFilename;
    }


}
