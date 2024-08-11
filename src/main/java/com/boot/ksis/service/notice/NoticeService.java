package com.boot.ksis.service.notice;

import com.boot.ksis.entity.Notice;
import com.boot.ksis.repository.notice.NoticeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    // 모든 공지 가져오기
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    // ID로 공지 가져오기
    public Optional<Notice> getNoticeById(Long id) {
        return noticeRepository.findById(id);
    }

    // 공지 생성
    public Notice createNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    // 공지 삭제
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }
}