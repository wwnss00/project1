package com.example.marketproject.service;

import com.example.marketproject.domain.entity.ChatRoom;
import com.example.marketproject.domain.entity.Post;
import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.response.ChatRoomResponse;
import com.example.marketproject.repository.ChatRoomRepository;
import com.example.marketproject.repository.PostRepository;
import com.example.marketproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 채팅방 생성 or 기존 방 반환
    @Transactional
    public ChatRoomResponse getOrCreateRoom(Long postId, Long buyerId) {

        // 이미 존재하는 방이면 그대로 반환
        return chatRoomRepository.findByPostIdAndBuyerId(postId, buyerId)
                .map(ChatRoomResponse::from)
                .orElseGet(() -> {
                    Post post = postRepository.findById(postId)
                            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

                    // 본인 게시글에 채팅 방지
                    if (post.getUser().getId().equals(buyerId)) {
                        throw new IllegalArgumentException("본인 게시글에는 채팅할 수 없습니다");
                    }

                    User buyer = userRepository.findById(buyerId)
                            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

                    ChatRoom room = ChatRoom.create(post, buyer, post.getUser());
                    return ChatRoomResponse.from(chatRoomRepository.save(room));
                });
    }

    // 내 채팅방 목록
    public List<ChatRoomResponse> getMyRooms(Long userId) {
        return chatRoomRepository.findByBuyerIdOrSellerIdOrderByCreatedAtDesc(userId, userId)
                .stream()
                .map(ChatRoomResponse::from)
                .toList();
    }
}
