package com.example.marketproject.controller;

import com.example.marketproject.dto.request.ChatMessageRequest;
import com.example.marketproject.dto.response.ChatMessageResponse;
import com.example.marketproject.security.CustomUserDetails;
import com.example.marketproject.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {

        // WebSocket 세션에서 userId 꺼내기
        Long senderId = (Long) headerAccessor.getSessionAttributes().get("userId");

        ChatMessageResponse response = chatMessageService.saveMessage(
                request.getRoomId(),
                senderId,
                request.getContent()
        );

        // 같은 채팅방 구독자들에게 전달
        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + request.getRoomId(),
                response
        );
    }
}