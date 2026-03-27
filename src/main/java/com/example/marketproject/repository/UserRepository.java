package com.example.marketproject.repository;

import com.example.marketproject.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    // 전체 유저 목록 (페이징)
    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = 'USER' ORDER BY u.createdAt DESC")
    Page<User> findAllUsers(Pageable pageable);


    // 유저 수 통계
    long countByIsDeletedFalse();
    long countByIsBannedTrue();
}
