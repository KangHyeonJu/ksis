package com.boot.ksis.service.log;

import com.boot.ksis.dto.log.AccessLogDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Log.AccessLog;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.log.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessLogService {
    private final AccessLogRepository accessLogRepository;
    private final AccountRepository accountRepository;

    public void saveAccessLog(AccessLogDTO accessLogDTO) {
        Account account = accountRepository.findById(accessLogDTO.getAccountId()).orElseThrow(null);

        AccessLog accessLog = new AccessLog();
        accessLog.setAccount(account);  // 조회된 Account 엔티티 설정
        accessLog.setCategory(accessLogDTO.getCategory());

        accessLogRepository.save(accessLog);
    }
}
