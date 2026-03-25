package com.example.marketproject.config;

import com.example.marketproject.security.CustomUserDetails;
import com.example.marketproject.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // CONNECT 시점에만 토큰 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token == null || !token.startsWith("Bearer ")) {
                throw new IllegalArgumentException("토큰이 없습니다");
            }

            String jwt = token.substring(7);
            if (!jwtTokenProvider.validateToken(jwt)) {
                throw new IllegalArgumentException("유효하지 않은 토큰입니다");
            }

            // userId를 WebSocket 세션에 저장
            Authentication auth = jwtTokenProvider.getAuthentication(jwt);
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            accessor.getSessionAttributes().put("userId", userDetails.getUserId());
        }

        return message;
    }
}
