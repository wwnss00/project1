package com.example.marketproject.service;

import com.example.marketproject.domain.entity.Post;
import com.example.marketproject.domain.entity.PostImage;
import com.example.marketproject.domain.entity.PostStatus;
import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.request.ChangeStatusRequest;
import com.example.marketproject.dto.request.CreatePostRequest;
import com.example.marketproject.dto.request.UpdatePostRequest;
import com.example.marketproject.dto.response.PostListResponse;
import com.example.marketproject.dto.response.PostResponse;
import com.example.marketproject.exception.PostNotFoundException;
import com.example.marketproject.exception.UnauthorizedException;
import com.example.marketproject.exception.UserNotFoundException;
import com.example.marketproject.repository.PostImageRepository;
import com.example.marketproject.repository.PostRepository;
import com.example.marketproject.repository.UserRepository;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final FileStorageService fileStorageService;

    //게시글 작성
    @Transactional
    public PostResponse createPost(CreatePostRequest request,List<MultipartFile> images,Long userId)
    throws IOException {

        // 1. User 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. Post 생성
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .location(request.getLocation())
                .user(user)
                .build();

        // 3. Post 저장
        Post savedPost = postRepository.save(post);

        // 4. 이미지 처리
        if (images != null && !images.isEmpty()) {
            int order = 1;
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    // 파일 저장
                    String storedFilename = fileStorageService.storeFile(image);

                    // PostImage 생성
                    PostImage postImage = PostImage.builder()
                            .post(savedPost)
                            .originalFilename(image.getOriginalFilename())
                            .storedFilename(storedFilename)
                            .filePath("/uploads/images/")
                            .fileSize(image.getSize())
                            .imageOrder(order)
                            .isThumbnail(order == 1)  // 첫 번째만 썸네일
                            .build();

                    savedPost.addImage(postImage);

                    postImageRepository.save(postImage);
                    order++;
                }
            }
        }
        return PostResponse.from(savedPost);
    }

    //게시글 상세 조회
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (post.isDeleted()) {
            throw new PostNotFoundException("삭제된 게시글입니다.");
        }

        post.increaseViewCount();
        return PostResponse.from(post);
    }

    //게시글 목록 조회
    @Transactional
    public Page<PostListResponse> getAllPosts(Pageable pageable) {
        return postRepository.findAllNotDeletedWithUser(pageable)
                .map(PostListResponse::from);

    }

    //게시글 수정
    @Transactional
    public PostResponse updatePost(Long postId, UpdatePostRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->new PostNotFoundException("게시글을 찾을 수 없습니다."));

        if (post.isDeleted()) {
            throw new IllegalStateException("삭제된 게시글은 수정할 수 없습니다.");
        }

        if(!post.isWriter(userId)) {
            throw new UnauthorizedException("게시글 수정 권한이 없습니다.");
        }

        post.update(
                request.getTitle(),
                request.getContent(),
                request.getPrice(),
                request.getLocation()
        );
        return PostResponse.from(post);
    }

    //상태 변경
    @Transactional
    public PostResponse changeStatus(Long postId, ChangeStatusRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));

        // 삭제 체크 추가!
        if (post.isDeleted()) {
            throw new IllegalStateException("삭제된 게시글의 상태를 변경할 수 없습니다.");
        }

        if(!post.isWriter(userId)) {
            throw new UnauthorizedException("상태 변경 권한이 없습니다.");
        }
        post.changeStatus(request.getStatus());
        return PostResponse.from(post);
    }

    //게시글 삭제
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다"));

        if (post.isDeleted()) {
            throw new IllegalStateException("이미 삭제된 게시글입니다");
        }

        if (!post.isWriter(userId)) {
            throw new UnauthorizedException("게시글 삭제 권한이 없습니다");
        }

        post.delete();
    }

    // 검색
    public Page<PostListResponse> searchPosts(
            String keyword,
            Integer minPrice,
            Integer maxPrice,
            String location,
            PostStatus status,
            Pageable pageable) {

        return postRepository.searchPosts(
                keyword, minPrice, maxPrice, location, status, pageable
        ).map(PostListResponse::from);
    }
}
