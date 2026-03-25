package com.example.marketproject.service;

import com.example.marketproject.domain.entity.Post;
import com.example.marketproject.domain.entity.User;
import com.example.marketproject.domain.entity.Wishlist;
import com.example.marketproject.repository.PostRepository;
import com.example.marketproject.repository.UserRepository;
import com.example.marketproject.repository.WishlistRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @InjectMocks
    private WishlistService wishlistService;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;


    // TODO 1. 찜 추가 성공
    @Test
    @DisplayName("찜 추가 성공")
    void addLike_success() {

        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .price(10000)
                .location("장소")
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);

        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

        User user = User.builder()
                .name("김준성")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("김준성")
                .email("jkl2085@naver.com")
                .phone("01022954328")
                .build();
        ReflectionTestUtils.setField(user,"id",1L);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(wishlistRepository.existsByUserIdAndPostId(user.getId(), post.getId())).willReturn(false);

        // when
        wishlistService.addLike(post.getId(), user.getId());

        // then
        verify(wishlistRepository, times(1)).save(any(Wishlist.class));

    }


    // TODO 2. 찜 추가 실패 - 중복
    @Test
    @DisplayName("찜 추가 실패 - 중복")
    void addLike_fail_duplicate() {

        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .price(10000)
                .location("장소")
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);

        given(postRepository.findById(post.getId())).willReturn(Optional.of(post));

        User user = User.builder()
                .name("김준성")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("김준성")
                .email("jkl2085@naver.com")
                .phone("01022954328")
                .build();
        ReflectionTestUtils.setField(user,"id",1L);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(wishlistRepository.existsByUserIdAndPostId(user.getId(), post.getId())).willReturn(true);


        // when & then
        assertThatThrownBy(() -> wishlistService.addLike(post.getId(), user.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 찜한 게시글입니다");

        verify(wishlistRepository, never()).save(any(Wishlist.class));
    }


    // TODO 3. 찜 취소 성공
    @Test
    @DisplayName("찜 취소 성공")
    void removeLike_success() {

        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .price(10000)
                .location("장소")
                .build();

        User user = User.builder()
                .name("김준성")
                .loginId("jkl2085")
                .password("!wanns2085")
                .nickname("김준성")
                .email("jkl2085@naver.com")
                .phone("01022954328")
                .build();

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .post(post)
                .build();

        given(wishlistRepository.findByUserIdAndPostId(user.getId(),post.getId())).willReturn(Optional.of(wishlist));

        // when
        wishlistService.removeLike(post.getId(), user.getId());

        // then
        verify(wishlistRepository, times(1)).delete(wishlist);



    }
}
