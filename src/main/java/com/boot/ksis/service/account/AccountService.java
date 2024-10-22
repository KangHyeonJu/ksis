package com.boot.ksis.service.account;

import com.boot.ksis.constant.Role;
import com.boot.ksis.dto.account.AccountDTO;
import com.boot.ksis.dto.account.AccountListDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Visit;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.account.VisitRepository;
import com.boot.ksis.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final SecretKeySpec keySpec;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final VisitRepository visitRepository;

    public boolean validateCredentials(String accountId, String password) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID must not be null or empty");
        }

        Account account = accountRepository.findByAccountId(accountId).orElse(null);
        if (account == null) {
            return false;
        }

        if (Boolean.TRUE.equals(account.getIsActive())) {
            throw new IllegalArgumentException("비활성화된 아이디 입니다.");
        }

        //방문자 수 추가
        Visit visit = new Visit();
        visit.setVisitDate(LocalDate.now());
        visitRepository.save(visit);

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

        // BusinessTel 암호화
        String encryptedBusinessTel = AESUtil.encrypt(dto.getBusinessTel(), keySpec);
        account.setBusinessTel(encryptedBusinessTel);

        account.setEmergencyTel(dto.getEmergencyTel());
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

        // BusinessTel 복호화
        String decryptedBusinessTel = AESUtil.decrypt(account.getBusinessTel(), keySpec);
        dto.setBusinessTel(decryptedBusinessTel);

        dto.setEmergencyTel(account.getEmergencyTel());
        dto.setEmail(account.getEmail());
        dto.setPosition(account.getPosition());
        dto.setGender(account.getGender());

        return dto;
    }

    public Page<AccountListDTO> getAccountList(int page, int size, String searchTerm, String searchCategory) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> accounts;

        // 검색어와 검색 카테고리에 따라 쿼리 다르게 처리
        if (searchTerm != null && !searchTerm.isEmpty()) {
            accounts = switch (searchCategory) {
                case "accountId" -> accountRepository.findByAccountIdContainingIgnoreCase(searchTerm, pageable);
                case "name" -> accountRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
//                case "businessTel" -> accountRepository.findByBusinessTelContainingIgnoreCase(searchTerm, pageable);
                case "businessTel" -> {
                    // 모든 계정을 먼저 조회한 후 복호화해서 검색어와 비교
                    Page<Account> allAccounts = accountRepository.findAll(pageable);
                    List<Account> filteredAccounts = allAccounts.getContent().stream()
                            .filter(account -> {
                                String decryptedBusinessTel;
                                try {
                                    decryptedBusinessTel = AESUtil.decrypt(account.getBusinessTel(), keySpec);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                return decryptedBusinessTel.contains(searchTerm);
                            })
                            .collect(Collectors.toList());
                    accounts = new PageImpl<>(filteredAccounts, pageable, allAccounts.getTotalElements());
                    yield accounts;
                }
                case "isActive" -> accountRepository.findByIsActive(Boolean.parseBoolean(searchTerm), pageable);
                default -> accountRepository.findAll(pageable);
            };
        } else {
            accounts = accountRepository.findAll(pageable);
        }

        Page<AccountListDTO> accountListDTOs = accounts
                .map(account -> {
                    AccountListDTO dto = new AccountListDTO();
                    dto.setAccountId(account.getAccountId());
                    dto.setName(account.getName());

                    // BusinessTel 복호화
                    String decryptedBusinessTel;
                    try {
                        decryptedBusinessTel = AESUtil.decrypt(account.getBusinessTel(), keySpec);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    dto.setBusinessTel(decryptedBusinessTel);

                    dto.setIsActive(account.getIsActive());
                    return dto;
                });
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

            // 업무 연락처 암호화
            if (updatedDto.getBusinessTel() != null) {
                String encryptedBusinessTel = AESUtil.encrypt(updatedDto.getBusinessTel(), keySpec);
                existingAccount.setBusinessTel(encryptedBusinessTel);
            }

            existingAccount.setEmergencyTel(updatedDto.getEmergencyTel());
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
