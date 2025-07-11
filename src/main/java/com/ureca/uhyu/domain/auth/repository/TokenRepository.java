package com.ureca.uhyu.domain.auth.repository;

import com.ureca.uhyu.domain.auth.entity.Token;
import com.ureca.uhyu.domain.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByUser(User user);

    void deleteByUser(User user);

    Optional<Token> findByRefreshToken(String refreshToken);

    Optional<Token> findByUserId(Long id);

    @Query("SELECT t.refreshToken FROM Token t WHERE t.user.id = :userId")
    String findRefreshTokenByUserId(@Param("userId") String userId);

    @Modifying // delete, update 쿼리에서 필수
    @Transactional // 메서드가 트랜잭션 에서 실행되도록 보장
    @Query("DELETE FROM Token t WHERE t.user.id =:userId")
    void deleteByUserId(@Param("userId") Long userId);
}
