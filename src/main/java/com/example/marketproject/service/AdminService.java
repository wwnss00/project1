package com.example.marketproject.service;

import com.example.marketproject.domain.entity.Post;
import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.response.AdminPostResponse;
import com.example.marketproject.dto.response.AdminUserResponse;
import com.example.marketproject.exception.PostNotFoundException;
import com.example.marketproject.exception.UserNotFoundException;
import com.example.marketproject.repository.PostRepository;
import com.example.marketproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 유저 목록
    public Page<AdminUserResponse> getUsers(Pageable pageable) {
        return userRepository.findAllUsers(pageable)
                .map(AdminUserResponse::from);
    }

    // 유저 강제 탈퇴
    @Transactional
    public void forceWithdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));
        user.withdraw();
    }

    // 유저 활동 정지
    @Transactional
    public void banUser(Long userId, LocalDateTime bannedUntil) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));
        user.ban(bannedUntil);
    }

    // 유저 정지 해제
    @Transactional
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));
        user.unban();
    }

    // 게시글 목록 (삭제된 것 포함)
    public Page<AdminPostResponse> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(AdminPostResponse::from);
    }

    // 게시글 강제 삭제
    @Transactional
    public void forceDeletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("존재하지 않는 게시글입니다."));
        post.delete();
    }

    // 통계
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.countByIsDeletedFalse());
        stats.put("bannedUsers", userRepository.countByIsBannedTrue());
        stats.put("totalPosts", postRepository.countByDeletedAtIsNull());
        return stats;
    }
}