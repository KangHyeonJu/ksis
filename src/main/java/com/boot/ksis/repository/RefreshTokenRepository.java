package com.boot.ksis.repository;

import com.boot.ksis.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByAccountId(String accountId);
    @Transactional
    void deleteByAccountId(String accountId);

    boolean existsByAccountId(String accountId);
}
