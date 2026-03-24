package com.example.marketproject.service;

import com.example.marketproject.domain.entity.Post;
import com.example.marketproject.domain.entity.User;
import com.example.marketproject.dto.request.CreatePostRequest;
import com.example.marketproject.dto.request.UpdatePostRequest;
import com.example.marketproject.dto.response.PostResponse;
import com.example.marketproject.exception.UnauthorizedException;
import com.example.marketproject.repository.PostImageRepository;
import com.example.marketproject.repository.PostRepository;
import com.example.marketproject.repository.UserRepository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostImageRepository postImageRepository;

    @Mock
    private FileStorageService fileStorageService;


    // TODO 1. 게시글 작성 성공 (이미지 없는 경우)
    @Test
    @DisplayName("게시글 작성 성공")
    void createPost_success() throws IOException {

        //given
        User user = User.builder()
                .name("김준성")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("김준성")
                .email("jkl2085@naver.com")
                .phone("01022954328")
                .build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        CreatePostRequest request = CreatePostRequest.builder()
                .title("제목")
                .content("내용")
                .price(10000)
                .location("장소")
                .build();

        Post post = Post.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .location(request.getLocation())
                .build();

        given(postRepository.save(any(Post.class))).willReturn(post);

        // when
        PostResponse response = postService.createPost(request,null,1L);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(request.getTitle());

        verify(postRepository, times(1)).save(any(Post.class));
    }


    // TODO 2. 게시글 수정 성공
    @Test
    @DisplayName("게시글 수정 성공")
    void updatePost_success() {

        // given
        User user = User.builder()
                .name("김준성")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("김준성")
                .email("jkl2085@naver.com")
                .phone("01022954328")
                .build();

        ReflectionTestUtils.setField(user, "id", 1L);

        Post post = Post.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .price(10000)
                .location("서울")
                .build();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        UpdatePostRequest request = UpdatePostRequest.builder()
                .title("제목목")
                .content("내용용")
                .price(5000)
                .location("장소소")
                .build();

        post.update(
                request.getTitle(),
                request.getContent(),
                request.getPrice(),
                request.getLocation()
        );

        // when
        PostResponse response = postService.updatePost(1L, request, 1L);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(request.getTitle());

    }


    // TODO 3. 게시글 수정 실패 - 권한 없음
    @Test
    @DisplayName("게시글 수정 실패 - 권한 없음")
    void updatePost_fail_unauthorized() {

        // given
        User user1 = User.builder()
                .name("김준성")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("김준성")
                .email("jkl2085@naver.com")
                .phone("01022954328")
                .build();

        ReflectionTestUtils.setField(user1, "id", 1L);

        Post post = Post.builder()
                .user(user1)
                .title("제목")
                .content("내용")
                .price(10000)
                .location("서울")
                .build();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        UpdatePostRequest request = UpdatePostRequest.builder()
                .title("제목목")
                .content("내용용")
                .price(5000)
                .location("인천")
                .build();

//
        // when & then
        assertThatThrownBy(() -> postService.updatePost(1L, request, 2L))
                 .isInstanceOf(UnauthorizedException.class)
                 .hasMessage("게시글 수정 권한이 없습니다.");

    }


    // TODO 4. 게시글 삭제 실패 - 권한 없음
    @Test
    @DisplayName("게시글 삭제 실패 - 권한 없음")
    void deletePost_fail_unauthorized() {

        // given
        User user1 = User.builder()
                .name("김준성")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("김준성")
                .email("jkl2085@naver.com")
                .phone("01022954328")
                .build();

        ReflectionTestUtils.setField(user1, "id", 1L);

        Post post = Post.builder()
                .user(user1)
                .title("제목")
                .content("내용")
                .price(10000)
                .location("서울")
                .build();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when & then
        assertThatThrownBy(() -> postService.deletePost(1L, 2L))
              .isInstanceOf(UnauthorizedException.class)
                .hasMessage("게시글 삭제 권한이 없습니다");


    }
}
