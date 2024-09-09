package com.boot.ksis.service.notice;

import com.boot.ksis.dto.notice.DeviceNoticeMapDTO;
import com.boot.ksis.dto.notice.NoticeDTO;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import com.boot.ksis.entity.Notice;
import com.boot.ksis.repository.notice.DeviceNoticeMapRepository;
import com.boot.ksis.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {


    private final NoticeRepository noticeRepository;
    private final DeviceNoticeMapRepository deviceNoticeMapRepository;

    // 공지 등록
    public NoticeDTO createNotice(NoticeDTO noticeDTO) {

        System.out.println("공지 등록 시작 : " + noticeDTO);

        Notice notice = new Notice(); // 새로운 공지 엔티티 생성
        notice.setTitle(noticeDTO.getTitle()); // 제목 설정
        notice.setContent(noticeDTO.getContent()); // 내용 설정
        notice.setCreatedBy(noticeDTO.getAccountId());//작성자 설정
        notice.setStartDate(noticeDTO.getStartDate()); // 노출 시작일 설정
        notice.setEndDate(noticeDTO.getEndDate()); // 노출 종료일 설정

        // 공지 저장
        Notice savedNotice = noticeRepository.save(notice);

        // 저장된 공지의 ID와 시간을 NoticeDTO에 설정
        noticeDTO.setNoticeId(savedNotice.getNoticeId());
        noticeDTO.setAccountId(savedNotice.getCreatedBy());
        noticeDTO.setRegTime(savedNotice.getRegTime()); // 등록 시간 설정
        noticeDTO.setUpdateTime(savedNotice.getUpdateTime()); // 수정 시간 설정
        System.out.println("공지 등록 끝 : " + savedNotice);
        return noticeDTO; // 저장된 공지 정보를 포함한 DTO 반환
    }

    // 공지 수정
    public NoticeDTO updateNotice(Long noticeId, NoticeDTO noticeDTO) {
        Optional<Notice> optionalNotice = noticeRepository.findById(noticeId); // 공지 아이디로 공지 검색

        if (optionalNotice.isPresent()) {
            Notice notice = optionalNotice.get(); // 공지 가져오기
            notice.setTitle(noticeDTO.getTitle()); // 제목 업데이트
            notice.setContent(noticeDTO.getContent()); // 내용 업데이트
            notice.setModifiedBy(noticeDTO.getAccountId());//수정한 사람 아이디 업데이트
            notice.setStartDate(noticeDTO.getStartDate()); // 노출 시작일 업데이트
            notice.setEndDate(noticeDTO.getEndDate()); // 노출 종료일 업데이트

            // 공지 수정 후 저장
            Notice updatedNotice = noticeRepository.save(notice);

            // 수정된 공지의 시간을 NoticeDTO에 설정
            noticeDTO.setUpdateTime(updatedNotice.getUpdateTime()); // 수정 시간 설정
            return noticeDTO; // 수정된 공지 정보를 포함한 DTO 반환
        } else {
            throw new RuntimeException("해당 공지를 찾을 수 없습니다."); // 공지가 없을 경우 오류 메시지
        }
    }

    // 공지 삭제
    public void deleteNotice(Long noticeId) {
        noticeRepository.deleteById(noticeId); // 공지 아이디로 삭제
    }

    // 공지 조회 (전체)
    public List<NoticeDTO> getAllNotices() {
        return noticeRepository.findAll().stream()
                .map(notice -> {
                    NoticeDTO dto = new NoticeDTO();
                    dto.setNoticeId(notice.getNoticeId()); // 공지 아이디 설정
                    dto.setAccountId(notice.getCreatedBy());//
                    dto.setTitle(notice.getTitle()); // 제목 설정
                    dto.setContent(notice.getContent()); // 내용 설정
                    dto.setStartDate(notice.getStartDate()); // 노출 시작일 설정
                    dto.setEndDate(notice.getEndDate()); // 노출 종료일 설정
                    dto.setRegTime(notice.getRegTime()); // 등록 시간 설정
                    dto.setUpdateTime(notice.getUpdateTime()); // 수정 시간 설정

                    // 작성자 정보 설정
                    if (notice.getAccount() != null) {
                        dto.setAccountId(notice.getAccount().getAccountId()); // 작성자 ID 설정
                        dto.setName(notice.getAccount().getName()); // 작성자 이름 설정
                    }

                    // 재생장치 정보 설정 (주석 처리된 부분이 있으므로 필요한 경우 주석 해제)
                    // dto.setDeviceName(notice.getDevice().getDeviceName()); // 재생장치 이름 설정

                    return dto; // 설정된 DTO 반환
                })
                .collect(Collectors.toList()); // DTO 리스트로 변환 후 반환
    }

    // 공지 상세조회
    public NoticeDTO getNoticeById(Long noticeId) {
        Optional<Notice> optionalNotice = noticeRepository.findById(noticeId); // 공지 아이디로 공지 검색

        if (optionalNotice.isPresent()) {
            Notice notice = optionalNotice.get();
            NoticeDTO dto = new NoticeDTO();
            dto.setNoticeId(notice.getNoticeId()); // 공지 아이디 설정
            dto.setTitle(notice.getTitle()); // 제목 설정
            dto.setContent(notice.getContent()); // 내용 설정
            dto.setStartDate(notice.getStartDate()); // 노출 시작일 설정
            dto.setEndDate(notice.getEndDate()); // 노출 종료일 설정
            dto.setRegTime(notice.getRegTime()); // 등록 시간 설정
            dto.setUpdateTime(notice.getUpdateTime()); // 수정 시간 설정

            // 작성자 정보 설정
            if (notice.getAccount() != null) {
                dto.setAccountId(notice.getAccount().getAccountId()); // 작성자 ID 설정
                dto.setName(notice.getAccount().getName()); // 작성자 이름 설정
            }

            // 재생장치 정보 설정 (주석 처리된 부분이 있으므로 필요한 경우 주석 해제)
            // dto.setDeviceName(notice.getDevice().getDeviceName()); // 재생장치 이름 설정

            return dto; // 설정된 DTO 반환
        } else {
            throw new RuntimeException("해당 공지를 찾을 수 없습니다."); // 공지가 없을 경우 오류 메시지
        }
    }
}