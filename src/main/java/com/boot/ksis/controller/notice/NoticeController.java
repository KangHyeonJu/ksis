package com.boot.ksis.controller.notice;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.notice.DeviceListDTO;
import com.boot.ksis.dto.notice.NoticeDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;


    // 공지 등록
    @CustomAnnotation(activityDetail = "공지 등록")
    @PostMapping
    public ResponseEntity<String> createNotice(Principal principal, @RequestBody NoticeDTO noticeDTO) {
        String accountId = principal.getName();

        noticeDTO.setAccountId(accountId);
        noticeDTO.setName(noticeDTO.getName());
        //공지 등록
        noticeService.createNotice(noticeDTO);
        return ResponseEntity.ok("공지가 정상적으로 등록되었습니다.");
    }

    // 공지 수정
    @PutMapping("/{noticeId}")
    public ResponseEntity<?> updateNotice(@PathVariable Long noticeId, @RequestBody NoticeDTO noticeDTO) {
        try {
            // 공지 수정 서비스 호출
            NoticeDTO updatedNotice = noticeService.updateNotice(noticeId, noticeDTO);
            return ResponseEntity.ok(updatedNotice); // 성공 시 수정된 공지 반환
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 공지 없음 예외 처리
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공지 수정에 실패했습니다.");
        }
    }

    // 공지 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<String> deleteNotice(@PathVariable Long noticeId) {
        try {
            // 공지 삭제 서비스 호출
            noticeService.deleteNotice(noticeId);
            return ResponseEntity.noContent().build(); // 성공 시 No Content 반환
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 공지 없음 예외 처리
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공지 삭제에 실패했습니다.");
        }
    }

    // 공지 조회 (전체)
    @GetMapping("/all")
    public ResponseEntity<?> getAllNotices() {
        try {
            // 공지 전체 조회 서비스 호출
            List<DeviceListDTO> notices = noticeService.getAllNotices();
            return ResponseEntity.ok(notices); // 성공 시 전체 공지 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공지 조회에 실패했습니다.");
        }
    }

    // 공지 상세조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<?> getNoticeById(@PathVariable Long noticeId) {
        try {
            // 공지 상세 조회 서비스 호출
            NoticeDTO notice = noticeService.getNoticeById(noticeId);
            return ResponseEntity.ok(notice); // 성공 시 상세 공지 반환
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 공지 없음 예외 처리
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공지 상세 조회에 실패했습니다.");
        }
    }
}