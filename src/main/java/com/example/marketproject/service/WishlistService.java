package com.example.marketproject.service;

import com.example.marketproject.domain.entity.Post;
import com.example.marketproject.domain.entity.User;
import com.example.marketproject.domain.entity.Wishlist;
import com.example.marketproject.dto.response.WishlistResponse;
import com.example.marketproject.exception.PostNotFoundException;
import com.example.marketproject.exception.UserNotFoundException;
import com.example.marketproject.exception.WishlistNotFoundException;
import com.example.marketproject.repository.PostRepository;
import com.example.marketproject.repository.UserRepository;
import com.example.marketproject.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 찜하기
    @Transactional
    public void addLike(Long postId, Long userId) {
        // 1. 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다"));

        // 2. 삭제된 게시글 확인
        if (post.isDeleted()) {
            throw new IllegalStateException("삭제된 게시글은 찜할 수 없습니다");
        }

        // 3. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다"));

        // 4. 중복 체크
        if (wishlistRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new IllegalStateException("이미 찜한 게시글입니다");
        }

        // 5. 찜 추가
        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .post(post)
                .build();

        try {
            wishlistRepository.save(wishlist);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("이미 찜한 게시글입니다");
        }
    }

    // 찜 취소
    @Transactional
    public void removeLike(Long postId, Long userId) {
        // 1. 찜 조회
        Wishlist wishlist = wishlistRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new WishlistNotFoundException("찜하지 않은 게시글입니다"));

        // 2. 삭제
        wishlistRepository.delete(wishlist);
    }

    // 내 찜 목록 조회
    public Page<WishlistResponse> getMyLikes(Long userId, Pageable pageable) {
        return wishlistRepository.findByUserIdWithPost(userId, pageable)
                .map(WishlistResponse::from);
    }

    public boolean isWished(Long postId, Long userId) {
        return wishlistRepository.existsByUserIdAndPostId(userId, postId);
    }
}
