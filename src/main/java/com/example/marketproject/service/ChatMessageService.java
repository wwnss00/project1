package com.example.marketproject.service;

import com.example.marketproject.domain.entity.ChatMessage;
import com.example.marketproject.domain.entity.ChatRoom;
import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.response.ChatMessageResponse;
import com.example.marketproject.repository.ChatMessageRepository;
import com.example.marketproject.repository.ChatRoomRepository;
import com.example.marketproject.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CHAT_MESSAGES_KEY = "chat:room:";
    private static final int REDIS_MESSAGE_LIMIT = 50; // 최근 50개만 캐싱

    @Transactional
    public ChatMessageResponse saveMessage(Long roomId, Long senderId, String content) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 채팅방 참여자 검증 (구매자 or 판매자만 가능)
        if (!chatRoom.getBuyer().getId().equals(senderId) &&
                !chatRoom.getSeller().getId().equals(senderId)) {
            throw new IllegalArgumentException("채팅방에 참여할 권한이 없습니다");
        }

        // MySQL 저장
        ChatMessage message = ChatMessage.create(chatRoom, sender, content);
        ChatMessage saved = chatMessageRepository.save(message);
        ChatMessageResponse response = ChatMessageResponse.from(saved);

        // Redis 캐싱
        cacheMessage(roomId, response);

        return response;
    }

    // 이전 메시지 조회 (Redis 우선, 없으면 MySQL)
    public List<ChatMessageResponse> getMessages(Long roomId) {
        String key = CHAT_MESSAGES_KEY + roomId;
        List<String> cached = redisTemplate.opsForList().range(key, 0, -1);

        if (cached != null && !cached.isEmpty()) {
            return cached.stream()
                    .map(json -> {
                        try {
                            return objectMapper.readValue(json, ChatMessageResponse.class);
                        } catch (Exception e) {
                            throw new RuntimeException("메시지 역직렬화 실패", e);
                        }
                    })
                    .toList();
        }

        // Redis에 없으면 MySQL에서 조회 후 Redis에 캐싱
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId);
        List<ChatMessageResponse> responses = messages.stream()
                .map(ChatMessageResponse::from)
                .toList();

        responses.forEach(r -> cacheMessage(roomId, r));
        return responses;
    }

    private void cacheMessage(Long roomId, ChatMessageResponse response) {
        String key = CHAT_MESSAGES_KEY + roomId;
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForList().rightPush(key, json);
            // 최근 50개만 유지
            redisTemplate.opsForList().trim(key, -REDIS_MESSAGE_LIMIT, -1);
        } catch (Exception e) {
            // Redis 캐싱 실패해도 MySQL에는 저장됐으니 무시
        }
    }
}
