package com.boot.ksis.service;

import com.boot.ksis.constant.Role;
import com.boot.ksis.dto.account.AccountDTO;
import com.boot.ksis.dto.account.AccountListDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final SecretKeySpec keySpec;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public boolean validateCredentials(String accountId, String password) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID must not be null or empty");
        }

        Account account = accountRepository.findByAccountId(accountId).orElse(null);
        if (account == null) {
            return false;
        }

        if (Boolean.TRUE.equals(account.getIsActive())) {
            return false;
        }

        return passwordEncoder.matches(password, account.getPassword());
    }

    public Account createAccount(AccountDTO dto) throws Exception {
        if (accountRepository.existsByAccountId(dto.getAccountId())) {
            throw new IllegalArgumentException("이미 가입된 아이디입니다.");
        }

        Account account = new Account();
        account.setAccountId(dto.getAccountId());
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setName(dto.getName());

        // BirthDate 암호화
        String encryptedBirthDate = AESUtil.encrypt(dto.getBirthDate(), keySpec);
        account.setBirthDate(encryptedBirthDate);

        // EmergencyTel 암호화
        String encryptedEmergencyTel = AESUtil.encrypt(dto.getEmergencyTel(), keySpec);
        account.setEmergencyTel(encryptedEmergencyTel);

        account.setBusinessTel(dto.getBusinessTel());
        account.setEmail(dto.getEmail());
        account.setPosition(dto.getPosition());
        account.setGender(dto.getGender());
        account.setIsActive(false);
        account.setRole(Role.ADMIN);
        return accountRepository.save(account);
    }

    public AccountDTO getAccountById(String accountId) throws Exception {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new Exception("Account not found"));
        AccountDTO dto = new AccountDTO();

        dto.setAccountId(account.getAccountId());
        dto.setName(account.getName());

        // BirthDate 복호화
        String decryptedBirthDate = AESUtil.decrypt(account.getBirthDate(), keySpec);
        dto.setBirthDate(decryptedBirthDate);

        // EmergencyTel 복호화
        String decryptedEmergencyTel = AESUtil.decrypt(account.getEmergencyTel(), keySpec);
        dto.setEmergencyTel(decryptedEmergencyTel);

        dto.setBusinessTel(account.getBusinessTel());
        dto.setEmail(account.getEmail());
        dto.setPosition(account.getPosition());
        dto.setGender(account.getGender());

        return dto;
    }

    public List<AccountListDTO> getAccountList() {
        List<Account> accounts = accountRepository.findAll();
        List<AccountListDTO> accountListDTOs = accounts.stream()
                .map(account -> {
                    AccountListDTO dto = new AccountListDTO();
                    dto.setAccountId(account.getAccountId());
                    dto.setName(account.getName());
                    dto.setBusinessTel(account.getBusinessTel());
                    dto.setIsActive(account.getIsActive());
                    return dto;
                })
                .collect(Collectors.toList());

        System.out.println("AccountListDto : " + accountListDTOs);
        return accountListDTOs;
    }

    public boolean toggleActiveStatus(String accountId, boolean isActive) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            // 상태를 반전시키는 로직
            account.setIsActive(!isActive);
            Account updatedAccount = accountRepository.save(account);

            return updatedAccount.getIsActive() == !isActive;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 예외 발생 시 실패로 간주
        }
    }

    public boolean updateAccount(String accountId, AccountDTO updatedDto) throws Exception {
        Account existingAccount = accountRepository.findById(accountId).orElse(null);
        if (existingAccount != null) {
            // 비밀번호 암호화
            if (updatedDto.getPassword() != null && !updatedDto.getPassword().isEmpty()) {
                existingAccount.setPassword(passwordEncoder.encode(updatedDto.getPassword()));
            }
            existingAccount.setName(updatedDto.getName());

            // 생년월일 암호화
            if (updatedDto.getBirthDate() != null) {
                String encryptedBirthDate = AESUtil.encrypt(updatedDto.getBirthDate(), keySpec);
                existingAccount.setBirthDate(encryptedBirthDate);
            }

            // 긴급 연락처 암호화
            if (updatedDto.getEmergencyTel() != null) {
                String encryptedEmergencyTel = AESUtil.encrypt(updatedDto.getEmergencyTel(), keySpec);
                existingAccount.setEmergencyTel(encryptedEmergencyTel);
            }

            existingAccount.setBusinessTel(updatedDto.getBusinessTel());
            existingAccount.setEmail(updatedDto.getEmail());
            existingAccount.setPosition(updatedDto.getPosition());
            existingAccount.setGender(updatedDto.getGender());

            logger.info("Updated account: {}", existingAccount);

            accountRepository.save(existingAccount);
            return true;
        }
        return false;
    }

    public static boolean checkPassword(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }
}
