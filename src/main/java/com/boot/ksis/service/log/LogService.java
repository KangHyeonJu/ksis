package com.boot.ksis.service.log;

import com.boot.ksis.dto.account.AccountNameDTO;
import com.boot.ksis.dto.log.LogDTO;
import com.boot.ksis.entity.Log.AccessLog;
import com.boot.ksis.entity.Log.ActivityLog;
import com.boot.ksis.entity.Log.UploadLog;
import com.boot.ksis.repository.log.AccessLogRepository;
import com.boot.ksis.repository.log.ActivityLogRepository;
import com.boot.ksis.repository.log.UploadLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {
    private final AccessLogRepository accessLogRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UploadLogRepository uploadLogRepository;

    public List<LogDTO> getAccessLogList(){
        List<LogDTO> logDTOList = new ArrayList<>();

        List<AccessLog> accessLogList = accessLogRepository.findAll();

        for(AccessLog accessLog : accessLogList){
            AccountNameDTO accountNameDTO = AccountNameDTO.builder()
                    .accountId(accessLog.getAccount().getAccountId())
                    .name(accessLog.getAccount().getName())
                    .build();

            LogDTO logDTO = LogDTO.builder()
                    .logId(accessLog.getAccessLogId())
                    .account(accountNameDTO)
                    .dateTime(accessLog.getRegTime())
                    .detail(String.valueOf(accessLog.getCategory()))
                    .build();

            logDTOList.add(logDTO);
        }
        return logDTOList;
    }

    public List<LogDTO> getActivityLogList(){
        List<LogDTO> logDTOList = new ArrayList<>();

        List<ActivityLog> activityLogList = activityLogRepository.findAll();

        for(ActivityLog activityLog : activityLogList){
            AccountNameDTO accountNameDTO = AccountNameDTO.builder()
                    .accountId(activityLog.getAccount().getAccountId())
                    .name(activityLog.getAccount().getName())
                    .build();

            LogDTO logDTO = LogDTO.builder()
                    .logId(activityLog.getActivityLogId())
                    .account(accountNameDTO)
                    .dateTime(activityLog.getDateTime())
                    .detail(activityLog.getActivityDetail())
                    .build();

            logDTOList.add(logDTO);
        }
        return logDTOList;
    }

    public List<LogDTO> getUploadLogList(){
        List<LogDTO> logDTOList = new ArrayList<>();

        List<UploadLog> uploadLogList = uploadLogRepository.findAll();

        for(UploadLog uploadLog : uploadLogList){
            AccountNameDTO accountNameDTO = AccountNameDTO.builder()
                    .accountId(uploadLog.getAccount().getAccountId())
                    .name(uploadLog.getAccount().getName())
                    .build();

            LogDTO logDTO = LogDTO.builder()
                    .logId(uploadLog.getUploadId())
                    .account(accountNameDTO)
                    .dateTime(uploadLog.getRegTime())
                    .detail(uploadLog.getMessage())
                    .build();

            logDTOList.add(logDTO);
        }
        return logDTOList;
    }
}
