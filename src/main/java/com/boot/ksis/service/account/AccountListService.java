package com.boot.ksis.service.account;

import com.boot.ksis.dto.account.AccountNameDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountListService {
    private final AccountRepository accountRepository;

    public List<AccountNameDTO> getAccountList(){
        List<Account> accountList = accountRepository.findByIsActive(false);

        List<AccountNameDTO> accountNameDTOList = new ArrayList<>();
        for(Account account : accountList){
            AccountNameDTO accountNameDTO = AccountNameDTO.builder()
                                                        .accountId(account.getAccountId())
                                                        .name(account.getName())
                                                        .build();
            accountNameDTOList.add(accountNameDTO);
        }
        return accountNameDTOList;
    }
}
