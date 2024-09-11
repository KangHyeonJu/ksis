package com.boot.ksis.service.notice;

import com.boot.ksis.dto.notice.DeviceListDTO;
import com.boot.ksis.dto.notice.DeviceNoticeDTO;
import com.boot.ksis.dto.notice.NoticeDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Device;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import com.boot.ksis.entity.Notice;
import com.boot.ksis.repository.DeviceRepository;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.notice.DeviceNoticeMapRepository;
import com.boot.ksis.repository.notice.NoticeRepository;
import com.boot.ksis.repository.signage.SignageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {


    private final NoticeRepository noticeRepository;
    private final AccountRepository accountRepository;
    private final DeviceNoticeMapRepository deviceNoticeMapRepository;
    private final DeviceRepository deviceRepository;
    private final SignageRepository signageRepository;

    // 공지 등록
    public void createNotice(NoticeDTO noticeDTO) {

        Account account = accountRepository.findByAccountId(noticeDTO.getAccountId()).orElse(null);

        Notice notice = new Notice(); // 새로운 공지 엔티티 생성
        notice.setTitle(noticeDTO.getTitle()); // 제목 설정
        notice.setContent(noticeDTO.getContent()); // 내용 설정
        notice.setAccount(account);//작성자 아이디
        notice.setCreatedBy(noticeDTO.getName());//작성자 이름
        notice.setStartDate(noticeDTO.getStartDate()); // 노출 시작일 설정
        notice.setEndDate(noticeDTO.getEndDate()); // 노출 종료일 설정

        // 공지 저장
        noticeRepository.save(notice);

        // 디바이스 리스트 작성
        for(Long deviceId : noticeDTO.getDeviceIds()) {
            Device device = deviceRepository.findById(deviceId).orElseThrow(()->new RuntimeException("디바이스를 찾을 수 없습니다 : "+deviceId));

            DeviceNoticeMap deviceNoticeMap = new DeviceNoticeMap();
            deviceNoticeMap.setDeviceId(deviceId);
            deviceNoticeMap.setNoticeId(notice.getNoticeId());

            deviceNoticeMap.setDevice(device);
            deviceNoticeMap.setNotice(notice);

            deviceNoticeMapRepository.save(deviceNoticeMap);
        }

    }

    // 공지 수정
    public NoticeDTO updateNotice(Long noticeId, NoticeDTO noticeDTO) {
        Optional<Notice> optionalNotice = noticeRepository.findById(noticeId);

        if (optionalNotice.isPresent()) {
            Notice notice = optionalNotice.get();
            notice.setTitle(noticeDTO.getTitle());
            notice.setContent(noticeDTO.getContent());
            notice.setModifiedBy(noticeDTO.getName()); // 수정자 설정
            notice.setStartDate(noticeDTO.getStartDate());
            notice.setEndDate(noticeDTO.getEndDate());

            // 기존 디바이스 매핑 삭제 후 새로운 매핑 저장
            deviceNoticeMapRepository.deleteByNoticeId(noticeId); // 기존 매핑 삭제
            saveDeviceNoticeMaps(noticeDTO.getDeviceIds(), notice); // 새로운 매핑 저장

            // 공지 수정 후 저장
            Notice updatedNotice = noticeRepository.save(notice);

            // 수정된 정보를 NoticeDTO에 반영
            noticeDTO.setUpdateTime(updatedNotice.getUpdateTime());
            return noticeDTO;
        } else {
            throw new RuntimeException("해당 공지를 찾을 수 없습니다.");
        }
    }

    // 공지 삭제
    public void deleteNotice(Long noticeId) {
        deviceNoticeMapRepository.deleteByNoticeId(noticeId); // 매핑된 디바이스 삭제
        noticeRepository.deleteById(noticeId); // 공지 삭제
    }

    // 공지 조회 (전체)
    public List<DeviceListDTO> getAllNotices() {
        List<Notice> notices = noticeRepository.findAll();
        List<DeviceListDTO> noticeDTOList = new ArrayList<>();

        for (Notice notice : notices) {
            DeviceListDTO dto = new DeviceListDTO();
            dto.setNoticeId(notice.getNoticeId());
            dto.setAccountId(notice.getAccount() != null ? notice.getAccount().getAccountId() : null);
            dto.setName(notice.getAccount() != null ? notice.getAccount().getName() : null);
            dto.setTitle(notice.getTitle());
            dto.setRegDate(notice.getRegTime());

            // 디바이스 정보 설정
            List<DeviceNoticeMap> deviceNoticeMaps = deviceNoticeMapRepository.findByNoticeId(notice.getNoticeId());

            List<DeviceNoticeDTO> deviceNoticeDTOList = new ArrayList<>();
            for (DeviceNoticeMap deviceNoticeMap : deviceNoticeMaps) {
                Device device = deviceNoticeMap.getDevice();

                DeviceNoticeDTO deviceNoticeDTO = new DeviceNoticeDTO(device.getDeviceId(),
                        device.getDeviceName());

                deviceNoticeDTOList.add(deviceNoticeDTO);
            }
            dto.setDeviceList(deviceNoticeDTOList);


            noticeDTOList.add(dto);

        }

        return noticeDTOList;
    }

    // 공지 상세 조회
    public NoticeDTO getNoticeById(Long noticeId) {
        Optional<Notice> optionalNotice = noticeRepository.findById(noticeId);

        if (optionalNotice.isPresent()) {
            Notice notice = optionalNotice.get();
            NoticeDTO dto = new NoticeDTO();
            dto.setNoticeId(notice.getNoticeId());
            dto.setTitle(notice.getTitle());
            dto.setContent(notice.getContent());
            dto.setStartDate(notice.getStartDate());
            dto.setEndDate(notice.getEndDate());
            dto.setRegTime(notice.getRegTime());
            dto.setUpdateTime(notice.getUpdateTime());

            // 작성자 정보 설정
            if (notice.getAccount() != null) {
                dto.setAccountId(notice.getAccount().getAccountId());
                dto.setName(notice.getAccount().getName());
            }

            // 디바이스 정보 설정
            List<Long> deviceIds = new ArrayList<>();
            List<DeviceNoticeMap> deviceNoticeMaps = deviceNoticeMapRepository.findByNoticeId(noticeId);
            for (DeviceNoticeMap deviceNoticeMap : deviceNoticeMaps) {
                deviceIds.add(deviceNoticeMap.getDeviceId());
            }
            dto.setDeviceIds(deviceIds);

            return dto;
        } else {
            throw new RuntimeException("해당 공지를 찾을 수 없습니다.");
        }
    }

    // 공지에 대한 디바이스 매핑 저장 로직
    private void saveDeviceNoticeMaps(List<Long> deviceIds, Notice notice) {
        for (Long deviceId : deviceIds) {
            Device device = deviceRepository.findById(deviceId)
                    .orElseThrow(() -> new RuntimeException("디바이스를 찾을 수 없습니다: " + deviceId));

            DeviceNoticeMap deviceNoticeMap = new DeviceNoticeMap();
            deviceNoticeMap.setDeviceId(deviceId);
            deviceNoticeMap.setNoticeId(notice.getNoticeId());
            deviceNoticeMap.setDevice(device);
            deviceNoticeMap.setNotice(notice);

            deviceNoticeMapRepository.save(deviceNoticeMap);
        }
    }
}