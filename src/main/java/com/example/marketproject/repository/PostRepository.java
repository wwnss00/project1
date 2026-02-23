package com.example.marketproject.repository;

import com.example.marketproject.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.user " +
            "WHERE p.deletedAt IS NULL " +
            "ORDER BY p.createdAt DESC")
    List<Post> findAllNotDeletedWithUser();

}
