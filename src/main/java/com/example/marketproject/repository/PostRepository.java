package com.example.marketproject.repository;

import com.example.marketproject.domain.entity.Post;
import com.example.marketproject.domain.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT p FROM Post p " +
            "WHERE p.deletedAt IS NULL " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findAllNotDeletedWithUser(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT p FROM Post p " +
            "WHERE p.deletedAt IS NULL " +
            "AND (:keyword IS NULL OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:location IS NULL OR p.location LIKE %:location%) " +
            "AND (:status IS NULL OR p.status = :status) " +
            "ORDER BY p.createdAt DESC")
    Page<Post> searchPosts(
            @Param("keyword") String keyword,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("location") String location,
            @Param("status") PostStatus status,
            Pageable pageable
    );

    Page<Post> findByUserId(Long userId, Pageable pageable);


}
