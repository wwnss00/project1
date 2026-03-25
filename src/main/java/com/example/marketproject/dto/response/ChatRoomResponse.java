package com.example.marketproject.dto.response;

import com.example.marketproject.domain.entity.ChatRoom;
import lombok.Builder;
import lombok.Getter;

// 채팅방 응답
@Getter
@Builder
public class ChatRoomResponse {
    private Long roomId;
    private Long postId;
    private String postTitle;
    private Long buyerId;
    private String buyerNickname;
    private Long sellerId;
    private String sellerNickname;

    public static ChatRoomResponse from(ChatRoom room) {
        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .postId(room.getPost().getId())
                .postTitle(room.getPost().getTitle())
                .buyerId(room.getBuyer().getId())
                .buyerNickname(room.getBuyer().getNickname())
                .sellerId(room.getSeller().getId())
                .sellerNickname(room.getSeller().getNickname())
                .build();
    }
}