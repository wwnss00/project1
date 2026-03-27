package com.example.marketproject.controller;

import com.example.marketproject.domain.entity.Post;
import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.request.BanRequest;
import com.example.marketproject.dto.response.AdminPostResponse;
import com.example.marketproject.dto.response.AdminUserResponse;
import com.example.marketproject.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    // 유저 목록
    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> getUsers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminService.getUsers(pageable));
    }

    // 유저 강제 탈퇴
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> forceWithdraw(@PathVariable Long userId) {
        adminService.forceWithdraw(userId);
        return ResponseEntity.noContent().build();
    }

    // 유저 활동 정지
    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<Void> banUser(
            @PathVariable Long userId,
            @RequestBody BanRequest request) {
        adminService.banUser(userId, request.getBannedUntil());
        return ResponseEntity.ok().build();
    }

    // 유저 정지 해제
    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable Long userId) {
        adminService.unbanUser(userId);
        return ResponseEntity.ok().build();
    }

    // 게시글 목록
    @GetMapping("/posts")
    public ResponseEntity<Page<AdminPostResponse>> getPosts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(adminService.getPosts(pageable));
    }

    // 게시글 강제 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> forceDeletePost(@PathVariable Long postId) {
        adminService.forceDeletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // 통계
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}
