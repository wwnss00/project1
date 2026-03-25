package com.example.marketproject.controller;


import com.example.marketproject.dto.response.WishlistResponse;
import com.example.marketproject.repository.WishlistRepository;
import com.example.marketproject.security.CustomUserDetails;
import com.example.marketproject.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WishlistController {

    private final WishlistService wishlistService;


    /**
     * 찜하기
     * POST /api/posts/{postId}/like
     */
    @PostMapping("/posts/{postId}/wishlist")
    public ResponseEntity<Void> addLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        wishlistService.addLike(postId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    /**
     * 찜 취소
     * DELETE /api/posts/{postId}/like
     */
    @DeleteMapping("/posts/{postId}/wishlist")
    public ResponseEntity<Void> removeLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        wishlistService.removeLike(postId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 내 찜 목록 조회
     * GET /api/me/likes
     */
    @GetMapping("/me/wishlist")
    public ResponseEntity<Page<WishlistResponse>> getMyLikes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<WishlistResponse> likes = wishlistService.getMyLikes(
                userDetails.getUserId(),
                pageable
        );

        return ResponseEntity.ok(likes);
    }

    @GetMapping("/posts/{postId}/wishlist/status")
    public ResponseEntity<Map<String, Boolean>> checkWishlist(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isWished = wishlistService.isWished(postId, userDetails.getUserId());
        return ResponseEntity.ok(Map.of("wished", isWished));
    }
}
