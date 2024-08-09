package com.boot.ksis.service;

import com.boot.ksis.constant.Role;
import com.boot.ksis.dto.AccountDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.util.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;


@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    private final SecretKeySpec keySpec;

    @Autowired
    public AccountService(AccountRepository accountRepository, SecretKeySpec keySpec) {
        this.accountRepository = accountRepository;
        this.keySpec = keySpec;
    }

//    public Account createAccount(AccountDTO dto) throws Exception {
//        Account account = new Account();
//        account.setAccountId(dto.getAccountId());
//        account.setPassword(hashPassword(dto.getPassword()));
//        account.setName(dto.getName());
//        account.setBirthDate(dto.getBirthDate());
//        account.setBusinessTel(dto.getBusinessTel());
//        account.setEmergencyTel(dto.getEmergencyTel());
//        account.setEmail(dto.getEmail());
//        account.setPosition(dto.getPosition());
//        account.setGender(dto.getGender());
//        account.setIsActive(false);
//        account.setRole(Role.ADMIN);
//        return accountRepository.save(account);
//    }

    public Account createAccount(AccountDTO dto) throws Exception {
        Account account = new Account();
        account.setAccountId(dto.getAccountId());
        account.setPassword(hashPassword(dto.getPassword()));
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

    public AccountDTO getAccount(String accountId) throws Exception {
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

    // 패스워드 해싱 메서드
    public static String hashPassword(String password) {return BCrypt.hashpw(password, BCrypt.gensalt());}

    public static boolean checkPassword(String password, String hashed) {return BCrypt.checkpw(password, hashed);}
}
