package com.example.marketproject.controller;

import com.example.marketproject.dto.request.ChangeStatusRequest;
import com.example.marketproject.dto.request.CreatePostRequest;
import com.example.marketproject.dto.request.UpdatePostRequest;
import com.example.marketproject.dto.response.PostListResponse;
import com.example.marketproject.dto.response.PostResponse;
import com.example.marketproject.security.CustomUserDetails;
import com.example.marketproject.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 1.게시글 작성
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        PostResponse response = postService.createPost(
                request,
                userDetails.getUserId()
        );
        return ResponseEntity.ok(response);
    }

    // 2.게시글 목록 조회
    @GetMapping
    public ResponseEntity<List<PostListResponse>> getAllposts() {
        long startTime = System.currentTimeMillis();  // 시작

        List<PostListResponse> posts = postService.getAllPosts();

        long endTime = System.currentTimeMillis();  // 종료
        long duration = endTime - startTime;

        System.out.println("실행 시간: " + duration + "ms");
        System.out.println("게시글 수: " + posts.size());

        return ResponseEntity.ok(posts);
    }

    // 3.게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        PostResponse response = postService.getPost(id);
        return ResponseEntity.ok(response);
    }

    // 4.게시글 수정
    @PatchMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        PostResponse response = postService.updatePost(id, request, userDetails.getUserId());

        return ResponseEntity.ok(response);
    }

    // 5.상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<PostResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatusRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        PostResponse response = postService.changeStatus(id, request, userDetails.getUserId());

        return ResponseEntity.ok(response);
    }

    // 6.게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        postService.deletePost(id, userDetails.getUserId());

        return ResponseEntity.noContent().build();
    }
}
