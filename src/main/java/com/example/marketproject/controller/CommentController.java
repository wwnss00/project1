package com.example.marketproject.controller;

import com.example.marketproject.dto.request.CreateCommentRequest;
import com.example.marketproject.dto.request.UpdateCommentRequest;
import com.example.marketproject.dto.response.CommentResponse;
import com.example.marketproject.security.CustomUserDetails;
import com.example.marketproject.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;


    // 1.댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        CommentResponse response = commentService.updateComment(commentId, request, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    // 2.댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        commentService.deleteComment(commentId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

}
