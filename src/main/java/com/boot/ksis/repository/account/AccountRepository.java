package com.boot.ksis.repository.account;

import com.boot.ksis.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
