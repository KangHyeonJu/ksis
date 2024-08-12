package com.boot.ksis.service.account;

import com.boot.ksis.entity.Account;
import com.boot.ksis.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountListService {
    private final AccountRepository accountRepository;

    public List<Account> getAccountList(){
        return accountRepository.findAll();
    }
}
