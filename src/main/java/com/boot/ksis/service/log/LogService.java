package com.boot.ksis.service.log;

import com.boot.ksis.constant.Category;
import com.boot.ksis.dto.account.AccountNameDTO;
import com.boot.ksis.dto.log.LogDTO;
import com.boot.ksis.entity.Log.AccessLog;
import com.boot.ksis.entity.Log.ActivityLog;
import com.boot.ksis.entity.Log.UploadLog;
import com.boot.ksis.repository.log.AccessLogRepository;
import com.boot.ksis.repository.log.ActivityLogRepository;
import com.boot.ksis.repository.log.UploadLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {
    private final AccessLogRepository accessLogRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UploadLogRepository uploadLogRepository;

    public Page<LogDTO> getAccessLogList(int page, int size, String searchTerm, String searchCategory, String startTime, String endTime) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));
        Page<AccessLog> accessLogPage;

        // 시작시간과 끝시간을 LocalDateTime으로 파싱
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (startTime != null && !startTime.isEmpty()) {
                LocalDate startDate = LocalDate.parse(startTime, formatter);
                startDateTime = startDate.atStartOfDay(); // 00:00:00으로 변환
            }
            if (endTime != null && !endTime.isEmpty()) {
                LocalDate endDate = LocalDate.parse(endTime, formatter);
                endDateTime = endDate.atTime(23, 59, 59); // 23:59:59으로 변환
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다.");
        }

        // 검색어가 있을 경우
        if (searchTerm != null && !searchTerm.isEmpty() || startDateTime != null || endDateTime != null) {
            switch (searchCategory) {
                case "account":
                    if (startDateTime != null && endDateTime != null) {
                        System.out.println("account 시작시간과 끝시간이 모두 있는 경우");
                        accessLogPage = accessLogRepository.findByAccount_AccountIdContainingIgnoreCaseAndRegTimeBetween(searchTerm, startDateTime, endDateTime, pageable);
                    } else if (startDateTime != null) {
                        System.out.println("account 시작시간만 있을 경우");
                        accessLogPage = accessLogRepository.findByAccount_AccountIdContainingIgnoreCaseAndRegTimeAfter(searchTerm, startDateTime, pageable);
                    } else if (endDateTime != null) {
                        System.out.println("account 끝시간만 있을 경우");
                        accessLogPage = accessLogRepository.findByAccount_AccountIdContainingIgnoreCaseAndRegTimeBefore(searchTerm, endDateTime, pageable);
                    } else {
                        System.out.println("account 시간이 없을 경우");
                        accessLogPage = accessLogRepository.findByAccount_AccountIdContainingIgnoreCase(searchTerm, pageable);
                    }
                    break;

                case "detail":
                    // Category를 enum으로 변환
                    Category category = null;
                    try {
                        category = Category.valueOf(searchTerm.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("잘못된 카테고리 값입니다.");
                    }

                    if (startDateTime != null && endDateTime != null) {
                        System.out.println("detail 시작시간과 끝시간이 모두 있는 경우");
                        accessLogPage = accessLogRepository.findByCategoryAndRegTimeBetween(category, startDateTime, endDateTime, pageable);
                    } else if (startDateTime != null) {
                        System.out.println("detail 시작시간만 있을 경우");
                        accessLogPage = accessLogRepository.findByCategoryAndRegTimeAfter(category, startDateTime, pageable);
                    } else if (endDateTime != null) {
                        System.out.println("detail 끝시간만 있을 경우");
                        accessLogPage = accessLogRepository.findByCategoryAndRegTimeBefore(category, endDateTime, pageable);
                    } else {
                        System.out.println("detail 시간이 없을 경우");
                        accessLogPage = accessLogRepository.findByCategory(category, pageable);
                    }

                    break;

                default:
                    accessLogPage = accessLogRepository.findAll(pageable);
            }
        } else {
            // 검색어가 없을 경우
            accessLogPage = accessLogRepository.findAll(pageable);
        }

        // DTO로 변환하여 반환
        return accessLogPage.map(accessLog -> {
            AccountNameDTO accountNameDTO = AccountNameDTO.builder()
                    .accountId(accessLog.getAccount().getAccountId())
                    .name(accessLog.getAccount().getName())
                    .build();

            return LogDTO.builder()
                    .logId(accessLog.getAccessLogId())
                    .account(accountNameDTO)
                    .dateTime(accessLog.getRegTime())
                    .detail(String.valueOf(accessLog.getCategory()))
                    .build();
        });
    }

    public Page<LogDTO> getActivityLogList(int page, int size, String searchTerm, String searchCategory, String startTime, String endTime) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateTime"));
        Page<ActivityLog> activityLogPage;

        // 시작시간과 끝시간을 LocalDateTime으로 파싱
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (startTime != null && !startTime.isEmpty()) {
                LocalDate startDate = LocalDate.parse(startTime, formatter);
                startDateTime = startDate.atStartOfDay(); // 00:00:00으로 변환
            }
            if (endTime != null && !endTime.isEmpty()) {
                LocalDate endDate = LocalDate.parse(endTime, formatter);
                endDateTime = endDate.atTime(23, 59, 59); // 23:59:59으로 변환
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다.");
        }

        // 검색어가 있을 경우
        if (searchTerm != null && !searchTerm.isEmpty() || startDateTime != null || endDateTime != null) {
            switch (searchCategory) {
                case "account":
                    if (startDateTime != null && endDateTime != null) {
                        System.out.println("account 시작시간과 끝시간이 모두 있는 경우");
                        activityLogPage = activityLogRepository.findByAccount_AccountIdContainingIgnoreCaseAndDateTimeBetween(searchTerm, startDateTime, endDateTime, pageable);
                    } else if (startDateTime != null) {
                        System.out.println("account 시작시간만 있을 경우");
                        activityLogPage = activityLogRepository.findByAccount_AccountIdContainingIgnoreCaseAndDateTimeAfter(searchTerm, startDateTime, pageable);
                    } else if (endDateTime != null) {
                        System.out.println("account 끝시간만 있을 경우");
                        activityLogPage = activityLogRepository.findByAccount_AccountIdContainingIgnoreCaseAndDateTimeBefore(searchTerm, endDateTime, pageable);
                    } else {
                        System.out.println("account 시간이 없을 경우");
                        activityLogPage = activityLogRepository.findByAccount_AccountIdContainingIgnoreCase(searchTerm, pageable);
                    }
                    break;

                case "detail":
                    if (startDateTime != null && endDateTime != null) {
                        System.out.println("detail 시작시간과 끝시간이 모두 있는 경우");
                        activityLogPage = activityLogRepository.findByActivityDetailContainingIgnoreCaseAndDateTimeBetween(searchTerm, startDateTime, endDateTime, pageable);
                    } else if (startDateTime != null) {
                        System.out.println("detail 시작시간만 있을 경우");
                        activityLogPage = activityLogRepository.findByActivityDetailContainingIgnoreCaseAndDateTimeAfter(searchTerm, startDateTime, pageable);
                    } else if (endDateTime != null) {
                        System.out.println("detail 끝시간만 있을 경우");
                        activityLogPage = activityLogRepository.findByActivityDetailContainingIgnoreCaseAndDateTimeBefore(searchTerm, endDateTime, pageable);
                    } else {
                        System.out.println("detail 시간이 없을 경우");
                        activityLogPage = activityLogRepository.findByActivityDetailContainingIgnoreCase(searchTerm, pageable);
                    }
                    break;

                default:
                    activityLogPage = activityLogRepository.findAll(pageable);
            }
        } else {
            // 검색어가 없을 경우
            activityLogPage = activityLogRepository.findAll(pageable);
        }

        // DTO로 변환하여 반환
        return activityLogPage.map(activityLog -> {
            AccountNameDTO accountNameDTO = AccountNameDTO.builder()
                    .accountId(activityLog.getAccount().getAccountId())
                    .name(activityLog.getAccount().getName())
                    .build();

            return LogDTO.builder()
                    .logId(activityLog.getActivityLogId())
                    .account(accountNameDTO)
                    .dateTime(activityLog.getDateTime())
                    .detail(activityLog.getActivityDetail())
                    .build();
        });
    }

    public Page<LogDTO> getUploadLogList(int page, int size, String searchTerm, String searchCategory, String startTime, String endTime){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));
        Page<UploadLog> uploadLogPage;

        // 시작시간과 끝시간을 LocalDateTime으로 파싱
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (startTime != null && !startTime.isEmpty()) {
                LocalDate startDate = LocalDate.parse(startTime, formatter);
                startDateTime = startDate.atStartOfDay(); // 00:00:00으로 변환
            }
            if (endTime != null && !endTime.isEmpty()) {
                LocalDate endDate = LocalDate.parse(endTime, formatter);
                endDateTime = endDate.atTime(23, 59, 59); // 23:59:59으로 변환
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다.");
        }

        // 검색어가 있을 경우
        if (searchTerm != null && !searchTerm.isEmpty() || startDateTime != null || endDateTime != null) {
            switch (searchCategory) {
                case "account":
                    if (startDateTime != null && endDateTime != null) {
                        System.out.println("account 시작시간과 끝시간이 모두 있는 경우");
                        // 시작시간과 끝시간이 모두 있을 경우
                        uploadLogPage = uploadLogRepository.findByAccount_AccountIdContainingIgnoreCaseAndRegTimeBetween(searchTerm, startDateTime, endDateTime, pageable);
                    } else if (startDateTime != null) {
                        System.out.println("account 시작시간만 있을 경우");
                        // 시작시간만 있을 경우
                        uploadLogPage = uploadLogRepository.findByAccount_AccountIdContainingIgnoreCaseAndRegTimeAfter(searchTerm, startDateTime, pageable);
                    } else if (endDateTime != null) {
                        System.out.println("account 끝시간만 있을 경우");
                        // 끝시간만 있을 경우
                        uploadLogPage = uploadLogRepository.findByAccount_AccountIdContainingIgnoreCaseAndRegTimeBefore(searchTerm, endDateTime, pageable);
                    } else {
                        System.out.println("account 시간이 없을 경우");
                        // 시간이 없을 경우
                        uploadLogPage = uploadLogRepository.findByAccount_AccountIdContainingIgnoreCase(searchTerm, pageable);
                    }
                    break;

                case "detail":
                    if (startDateTime != null && endDateTime != null) {
                        System.out.println("detail 시작시간과 끝시간이 모두 있는 경우");
                        uploadLogPage = uploadLogRepository.findByMessageContainingIgnoreCaseAndRegTimeBetween(searchTerm, startDateTime, endDateTime, pageable);
                    } else if (startDateTime != null) {
                        System.out.println("detail 시작시간만 있을 경우");
                        uploadLogPage = uploadLogRepository.findByMessageContainingIgnoreCaseAndRegTimeAfter(searchTerm, startDateTime, pageable);
                    } else if (endDateTime != null) {
                        System.out.println("detail 끝시간만 있을 경우");
                        uploadLogPage = uploadLogRepository.findByMessageContainingIgnoreCaseAndRegTimeBefore(searchTerm, endDateTime, pageable);
                    } else {
                        System.out.println("detail 시간이 없을 경우");
                        uploadLogPage = uploadLogRepository.findByMessageContainingIgnoreCase(searchTerm, pageable);
                    }
                    break;

                default:
                    uploadLogPage = uploadLogRepository.findAll(pageable);
            }
        } else {
            // 검색어가 없을 경우
            uploadLogPage = uploadLogRepository.findAll(pageable);
        }

        // DTO로 변환하여 반환
        return uploadLogPage.map(uploadLog -> {
            AccountNameDTO accountNameDTO = AccountNameDTO.builder()
                    .accountId(uploadLog.getAccount().getAccountId())
                    .name(uploadLog.getAccount().getName())
                    .build();

            return LogDTO.builder()
                    .logId(uploadLog.getUploadId())
                    .account(accountNameDTO)
                    .dateTime(uploadLog.getRegTime())
                    .detail(uploadLog.getMessage())
                    .build();
        });
    }
}
