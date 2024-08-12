package com.boot.ksis.service.notice;

import com.boot.ksis.dto.NoticeDTO;
import com.boot.ksis.dto.NoticeFormDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Notice;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.notice.NoticeRepository;
import com.boot.ksis.repository.pc.PcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    public Notice createNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    public Optional<Notice> getNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId);
    }

    public Notice updateNotice(Long noticeId, Notice updatedNotice) {
        if (noticeRepository.existsById(noticeId)) {
            updatedNotice.setNoticeId(noticeId);
            return noticeRepository.save(updatedNotice);
        }
        return null; // 또는 예외를 던질 수 있습니다.
    }

    public void deleteNotice(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }
}