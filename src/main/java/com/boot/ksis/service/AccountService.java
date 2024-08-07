package com.boot.ksis.service;

import com.boot.ksis.constant.Role;
import com.boot.ksis.dto.AccountDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    private static SecretKey secretKey;

    public AccountService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            secretKey = keyGen.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 팩토리 메서드
//    public Account createAccount(AccountDTO dto) throws Exception {
//        Account account = new Account();
//        account.setAccountId(dto.getAccountId());
//        account.setPassword(hashPassword(dto.getPassword()));
//        account.setName(dto.getName());
//        account.setBirthdate((dto.getBirthdate()));
//        account.setEmergencyTel((dto.getEmergencyTel()));
//        account.setBusinessTel(dto.getBusinessTel());
//        account.setEmail(dto.getEmail());
//        account.setPosition(dto.getPosition());
//        account.setGender(dto.getGender());
//        account.setIsActive(true);
//        account.setRole(Role.ADMIN);
//        return accountRepository.save(account);
//    }

    public Account createAccount(AccountDTO dto) throws Exception {
        Account account = new Account();
        account.setAccountId(dto.getAccountId());
        account.setPassword(hashPassword(dto.getPassword()));
        account.setName(dto.getName());
        account.setBirthDate(dto.getBirthDate());
        account.setBusinessTel(dto.getBusinessTel());
        account.setEmergencyTel(dto.getEmergencyTel());
        account.setEmail(dto.getEmail());
        account.setPosition(dto.getPosition());
        account.setGender(dto.getGender());
        account.setIsActive(false);
        account.setRole(Role.ADMIN);
        return accountRepository.save(account);
    }

    // 패스워드 해싱 메서드
    public static String hashPassword(String password) {return BCrypt.hashpw(password, BCrypt.gensalt());}

    public static boolean checkPassword(String password, String hashed) {return BCrypt.checkpw(password, hashed);}
}
