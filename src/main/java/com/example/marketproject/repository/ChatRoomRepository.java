package com.example.marketproject.repository;

import com.example.marketproject.domain.entity.ChatMessage;
import com.example.marketproject.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 이미 존재하는 채팅방 조회 (중복 생성 방지용)
    Optional<ChatRoom> findByPostIdAndBuyerId(Long postId, Long buyerId);

    // 내 채팅방 목록 (구매자 or 판매자로 참여한 방 전체)
    List<ChatRoom> findByBuyerIdOrSellerIdOrderByCreatedAtDesc(Long buyerId, Long sellerId);
}

