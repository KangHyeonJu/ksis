package com.boot.ksis.controller.notice;

import com.boot.ksis.dto.notice.NoticeDTO;
import com.boot.ksis.service.notice.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // React 애플리케이션의 주소s
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 공지 등록
    @PostMapping
    public ResponseEntity<NoticeDTO> createNotice(@RequestBody NoticeDTO noticeDTO) {
        // 공지 등록 서비스 호출
        NoticeDTO createdNotice = noticeService.createNotice(noticeDTO);
        return ResponseEntity.ok(createdNotice); // 성공 시 등록된 공지 반환
    }

    // 공지 수정
    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeDTO> updateNotice(@PathVariable Long noticeId, @RequestBody NoticeDTO noticeDTO) {
        // 공지 수정 서비스 호출
        NoticeDTO updatedNotice = noticeService.updateNotice(noticeId, noticeDTO);
        return ResponseEntity.ok(updatedNotice); // 성공 시 수정된 공지 반환
    }

    // 공지 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) {
        // 공지 삭제 서비스 호출
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.noContent().build(); // 성공 시 No Content 반환
    }

    // 공지 조회 (전체)
    @GetMapping
    public ResponseEntity<List<NoticeDTO>> getAllNotices() {
        // 공지 전체 조회 서비스 호출
        List<NoticeDTO> notices = noticeService.getAllNotices();
        return ResponseEntity.ok(notices); // 성공 시 전체 공지 반환
    }

    // 공지 상세조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeDTO> getNoticeById(@PathVariable Long noticeId) {
        // 공지 상세 조회 서비스 호출
        NoticeDTO notice = noticeService.getNoticeById(noticeId);
        return ResponseEntity.ok(notice); // 성공 시 상세 공지 반환
    }
}
