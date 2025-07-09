package com.ureca.uhyu.domain.auth.repository;

import com.ureca.uhyu.domain.auth.entity.Token;
import com.ureca.uhyu.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByUser(User user);

    void deleteByUser(User user);

    Optional<Token> findByRefreshToken(String refreshToken);

    Optional<Token> findByUserId(Long id);
}