package com.boot.ksis.repository.account;

import com.boot.ksis.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByAccountId(String accountId);
    boolean existsByAccountId(String accountId);


}
