package com.example.marketproject.service;

import com.example.marketproject.domain.entity.Comment;
import com.example.marketproject.domain.entity.Post;
import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.request.CreateCommentRequest;
import com.example.marketproject.dto.request.UpdateCommentRequest;
import com.example.marketproject.dto.response.CommentResponse;
import com.example.marketproject.exception.CommentNotFoundException;
import com.example.marketproject.exception.PostNotFoundException;
import com.example.marketproject.exception.UnauthorizedException;
import com.example.marketproject.exception.UserNotFoundException;
import com.example.marketproject.repository.CommentRepository;
import com.example.marketproject.repository.PostRepository;
import com.example.marketproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 댓글 작성
    @Transactional
    public CommentResponse createComment(CreateCommentRequest request, Long userId, Long postId) {
        // 1.게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다"));

        // 2.삭제된 게시글 확인
        if (post.isDeleted()) {
            throw new IllegalStateException("삭제된 게시글에는 댓글을 작성할 수 없습니다");
        }

        // 3.사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 4.댓글 생성
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(request.getContent())
                .build();

        // 5.저장
        Comment savedComment = commentRepository.save(comment);
        return CommentResponse.from(savedComment);
    }

    // 댓글 목록 조회 (게시글별)
    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdWithUser(postId)
                .stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    //댓글 수정
    @Transactional
    public CommentResponse updateComment(Long commentId, UpdateCommentRequest request, Long userId) {
        // 1.댓글 조회
        Comment comment =  commentRepository.findById(commentId)
                .orElseThrow(() ->new CommentNotFoundException("댓글을 찾을 수 없습니다."));
        // 2.삭제 확인
        if (comment.isDeleted()) {
            throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다.");
        }

        // 3.권한 확인
        if (!comment.isWriter(userId)) {
            throw new UnauthorizedException("댓글 수정 권한이 없습니다.");
        }

        // 4.수정
        comment.update(request.getContent());

        return CommentResponse.from(comment);

    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {

        // 1.댓글 조회
        Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다"));

        // 2.삭제 확인
        if (comment.isDeleted()) {
            throw new IllegalStateException("이미 삭제된 댓글입니다");
        }

        // 3.권한 확인
        if (!comment.isWriter(userId)) {
            throw new UnauthorizedException("댓글 삭제 권한이 없습니다");
        }

        // 4.소프트 삭제
        comment.delete();
        }
    }