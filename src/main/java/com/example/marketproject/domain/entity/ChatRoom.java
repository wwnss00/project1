package com.example.marketproject.domain.entity;

import com.example.marketproject.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_rooms",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"post_id", "buyer_id"}  // 같은 게시글에 같은 구매자가 방을 중복 생성 방지
        )
)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;      // 구매자 (채팅 건 사람)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;     // 판매자 (게시글 작성자)

    public static ChatRoom create(Post post, User buyer, User seller) {
        ChatRoom room = new ChatRoom();
        room.post = post;
        room.buyer = buyer;
        room.seller = seller;
        return room;
    }
}
