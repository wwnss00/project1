package com.example.marketproject.controller;

import com.example.marketproject.domain.entity.PostStatus;
import com.example.marketproject.dto.request.ChangeStatusRequest;
import com.example.marketproject.dto.request.CreatePostRequest;
import com.example.marketproject.dto.request.UpdatePostRequest;
import com.example.marketproject.dto.response.PostListResponse;
import com.example.marketproject.dto.response.PostResponse;
import com.example.marketproject.security.CustomUserDetails;
import com.example.marketproject.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 1.게시글 작성
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @ModelAttribute CreatePostRequest request,
            @RequestParam(required = false) List<MultipartFile> images, // 이미지 파일들
            @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {

        PostResponse response = postService.createPost(
                request,
                images,
                userDetails.getUserId()
        );
        return ResponseEntity.ok(response);
    }

    // 2.게시글 목록 조회
    @GetMapping
    public ResponseEntity<Page<PostListResponse>> getAllposts(
            @PageableDefault(
                    size = 10,                                      //기본 10개
                    sort = "createdAt",                              //정렬 기준
                    direction = Sort.Direction.DESC                 //내림차순
            ) Pageable pageable) {

        Page<PostListResponse> posts = postService.getAllPosts(pageable);
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

    // 7.검색
    @GetMapping("/search")
    public ResponseEntity<Page<PostListResponse>> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) PostStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<PostListResponse> posts = postService.searchPosts(
                keyword, minPrice, maxPrice, location, status, pageable
        );

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<PostListResponse>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<PostListResponse> posts = postService.getMyPosts(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(posts);
    }
}
