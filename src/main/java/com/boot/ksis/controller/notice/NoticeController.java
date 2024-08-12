package com.boot.ksis.controller.notice;

import com.boot.ksis.entity.Notice;
import com.boot.ksis.service.notice.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = "http://localhost:3000") // React 애플리케이션의 주소
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PostMapping
    public ResponseEntity<Notice> createNotice(@RequestBody Notice notice) {
        System.out.println("notice??"+notice.getStartDate());
        Notice createdNotice = noticeService.createNotice(notice);
        return new ResponseEntity<>(createdNotice, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Notice>> getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        return new ResponseEntity<>(notices, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notice> getNoticeById(@PathVariable("id") Long noticeId) {
        Optional<Notice> notice = noticeService.getNoticeById(noticeId);
        return notice.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notice> updateNotice(@PathVariable("id") Long noticeId, @RequestBody Notice updatedNotice) {
        Notice notice = noticeService.updateNotice(noticeId, updatedNotice);
        return (notice != null) ? new ResponseEntity<>(notice, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("id") Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}