package com.boot.ksis.repository.account;

import com.boot.ksis.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Object> findById(Long accountId);
}
