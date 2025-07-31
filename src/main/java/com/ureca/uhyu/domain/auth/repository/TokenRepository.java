package com.ureca.uhyu.domain.auth.repository;

import com.ureca.uhyu.domain.auth.entity.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByUserId(Long id);

    @Transactional
    @Modifying
    void deleteByUser_Id(Long userId);
}
