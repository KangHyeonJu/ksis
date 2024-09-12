package com.boot.ksis.service.log;

import com.boot.ksis.dto.log.UploadLogDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Log.UploadLog;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.log.UploadLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadLogService {

    private final UploadLogRepository uploadLogRepository;
    private final AccountRepository accountRepository;

    public void uploadLog(UploadLogDTO uploadLogDTO){

        Account account = accountRepository.findByAccountId(uploadLogDTO.getAccountId()).orElseThrow(null);

        UploadLog uploadLog = new UploadLog();
        uploadLog.setAccount(account);
        uploadLog.setMessage(uploadLogDTO.getMessage());
        uploadLogRepository.save(uploadLog);

    }
}
