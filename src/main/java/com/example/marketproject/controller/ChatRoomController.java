package com.example.marketproject.controller;

import com.example.marketproject.dto.request.ChatRoomCreateRequest;
import com.example.marketproject.dto.response.ChatMessageResponse;
import com.example.marketproject.dto.response.ChatRoomResponse;
import com.example.marketproject.security.CustomUserDetails;
import com.example.marketproject.service.ChatMessageService;
import com.example.marketproject.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    // 채팅방 생성 or 입장
    @PostMapping
    public ResponseEntity<ChatRoomResponse> getOrCreateRoom(
            @RequestBody ChatRoomCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ChatRoomResponse response = chatRoomService.getOrCreateRoom(
                request.getPostId(),
                userDetails.getUserId()
        );
        return ResponseEntity.ok(response);
    }

    // 내 채팅방 목록
    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(chatRoomService.getMyRooms(userDetails.getUserId()));
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(chatMessageService.getMessages(roomId));
    }
}
