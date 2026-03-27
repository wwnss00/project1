package com.example.marketproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BanRequest {
    private LocalDateTime bannedUntil;  // null이면 영구정지
}
