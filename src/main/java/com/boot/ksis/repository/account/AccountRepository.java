package com.boot.ksis.repository.account;

import com.boot.ksis.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByAccountId(String accountId);
    boolean existsByAccountId(String accountId);

    List<Account> findByIsActive(boolean isActive);

    Page<Account> findByAccountIdContainingIgnoreCase(String accountId, Pageable pageable);
    Page<Account> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Account> findByBusinessTelContainingIgnoreCase(String businessTel, Pageable pageable);
    Page<Account> findByIsActive(boolean isActive, Pageable pageable);
}
