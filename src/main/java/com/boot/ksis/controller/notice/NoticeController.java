package com.boot.ksis.controller.notice;

import com.boot.ksis.dto.NoticeDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Notice;
import com.boot.ksis.service.account.AccountListService;
import com.boot.ksis.service.notice.NoticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;
    private final AccountListService accountService;

    public NoticeController(NoticeService noticeService, AccountListService accountService) {
        this.noticeService = noticeService;
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<NoticeDTO>> getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        List<NoticeDTO> noticeDTOs = notices.stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(noticeDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeDTO> getNoticeById(@PathVariable Long id) {
        Optional<Notice> notice = noticeService.getNoticeById(id);
        return notice.map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<NoticeDTO> createNotice(@RequestBody NoticeDTO noticeDTO) {
        System.out.println("Received notice data: " + noticeDTO);

        try {
            Notice notice = convertToEntity(noticeDTO);
            Notice savedNotice = noticeService.createNotice(notice);
            NoticeDTO savedNoticeDTO = convertToDTO(savedNotice);
            return ResponseEntity.status(201).body(savedNoticeDTO);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    private Notice convertToEntity(NoticeDTO dto) {
        Notice notice = new Notice();
        if (dto.getNoticeId() != null) {
            notice.setNoticeId(dto.getNoticeId());
        }

        if (dto.getAccountId() != null) {
            Optional<Account> account = accountService.getAccountById(dto.getAccountId());
            if (account.isPresent()) {
                notice.setAccount(account.get());
            } else {
                throw new RuntimeException("Account not found for id: " + dto.getAccountId());
            }
        }

        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());

        // Parse startDate and endDate
        try {
            if (dto.getStartDate() != null && !dto.getStartDate().isEmpty()) {
                notice.setStartDate(LocalDateTime.parse(dto.getStartDate()));
            }
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid start date format: " + dto.getStartDate(), e);
        }

        try {
            if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
                notice.setEndDate(LocalDateTime.parse(dto.getEndDate()));
            }
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid end date format: " + dto.getEndDate(), e);
        }

        return notice;
    }

    private NoticeDTO convertToDTO(Notice notice) {
        NoticeDTO dto = new NoticeDTO();
        dto.setNoticeId(notice.getNoticeId());
        dto.setTitle(notice.getTitle());
        dto.setContent(notice.getContent());

        // Convert LocalDateTime to String
        if (notice.getStartDate() != null) {
            dto.setStartDate(LocalDateTime.parse(notice.getStartDate().toString()));
        }
        if (notice.getEndDate() != null) {
            dto.setEndDate(LocalDateTime.parse(notice.getEndDate().toString()));
        }

        if (notice.getAccount() != null) {
            dto.setAccountId(notice.getAccount().getAccountId());
        }

        return dto;
    }
}