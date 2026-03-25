package com.example.marketproject.dto.request;

import lombok.Getter;

// 메시지 전송 요청 (클라이언트 → 서버)
@Getter
public class ChatMessageRequest {
    private Long roomId;
    private String content;
}
