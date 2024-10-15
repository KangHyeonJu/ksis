package com.boot.ksis.service.notice;

import com.boot.ksis.constant.Role;
import com.boot.ksis.dto.notice.*;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Device;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import com.boot.ksis.entity.Notice;
import com.boot.ksis.repository.DeviceRepository;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.notice.DeviceNoticeMapRepository;
import com.boot.ksis.repository.notice.NoticeRepository;
import jakarta.transaction.Transactional;
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
        notice.setActive(notice.isActive());

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


    //ADMIN 공지 조회 (활성화 된 것 전체)
    public List<DeviceListDTO> getAllActiveNotices() {
        // 활성화된 공지들만 조회
        List<Notice> notices = noticeRepository.findByIsActiveOrderByRegTimeDesc(true);
        return convertNoticesToDTO(notices);
    }

    //ADMIN 공지 조회(비활성화 전체)
    public List<DeviceListDTO> getAllNoneActiveNotices() {
        // 활성화된 공지들만 조회
        List<Notice> notices = noticeRepository.findByIsActiveOrderByRegTimeDesc(false);
        return convertNoticesToDTO(notices);
    }

    //USER 공지 조회 (활성화 본인 공지)
    public List<DeviceListDTO> getUserActiveNotices(String accountId) {
        List<Notice> notices = noticeRepository.findByAccount_AccountIdAndIsActiveOrderByRegTimeDesc(accountId, true);
        List<Notice> adminNotices = noticeRepository.findByAccount_RoleAndIsActiveOrderByRegTimeDesc(Role.ADMIN, true);
        return convertUserNoticesToDTO(notices, adminNotices);
    }

    //USER 공지 조회 (비활성화 본인 공지)
    public List<DeviceListDTO> getUserNoneActiveNotices(String accountId) {
        List<Notice> notices = noticeRepository.findByAccount_AccountIdAndIsActiveOrderByRegTimeDesc(accountId, false);
        List<Notice> adminNotices = noticeRepository.findByAccount_RoleAndIsActiveOrderByRegTimeDesc(Role.ADMIN, true);
        return convertUserNoticesToDTO(notices, adminNotices);
    }

    // 본인 공지 및 관리자 공지 DTO 변환
    private List<DeviceListDTO> convertUserNoticesToDTO(List<Notice> userNotices, List<Notice> adminNotices) {
        List<DeviceListDTO> noticeDTOList = new ArrayList<>();

        // 본인 공지와 관리자 공지를 모두 합칩니다.
        List<Notice> combinedNotices = new ArrayList<>(userNotices);
        combinedNotices.addAll(adminNotices);

        for (Notice notice : combinedNotices) {
            DeviceListDTO dto = new DeviceListDTO();
            dto.setNoticeId(notice.getNoticeId());
            dto.setAccountId(notice.getAccount() != null ? notice.getAccount().getAccountId() : null);
            dto.setName(notice.getAccount() != null ? notice.getAccount().getName() : null);
            dto.setRole(notice.getAccount() != null ? notice.getAccount().getRole() : null);
            dto.setTitle(notice.getTitle());
            dto.setRegDate(notice.getRegTime());

            // 디바이스 정보 설정
            List<DeviceNoticeMap> deviceNoticeMaps = deviceNoticeMapRepository.findByNoticeId(notice.getNoticeId());
            List<DeviceNoticeDTO> deviceNoticeDTOList = new ArrayList<>();
            for (DeviceNoticeMap deviceNoticeMap : deviceNoticeMaps) {
                Device device = deviceNoticeMap.getDevice();
                DeviceNoticeDTO deviceNoticeDTO = new DeviceNoticeDTO(device.getDeviceId(), device.getDeviceName());
                deviceNoticeDTOList.add(deviceNoticeDTO);
            }
            dto.setDeviceList(deviceNoticeDTOList);

            noticeDTOList.add(dto);
        }

        return noticeDTOList;
    }

    // 전체 공지 목록을 DTO로 변환
    private List<DeviceListDTO> convertNoticesToDTO(List<Notice> notices) {
        List<DeviceListDTO> noticeDTOList = new ArrayList<>();

        for (Notice notice : notices) {
            DeviceListDTO dto = new DeviceListDTO();
            dto.setNoticeId(notice.getNoticeId());
            dto.setAccountId(notice.getAccount() != null ? notice.getAccount().getAccountId() : null);
            dto.setName(notice.getAccount() != null ? notice.getAccount().getName() : null);
            dto.setRole(notice.getAccount() != null ? notice.getAccount().getRole() : null);
            dto.setTitle(notice.getTitle());
            dto.setRegDate(notice.getRegTime());

            // 디바이스 정보 설정
            List<DeviceNoticeMap> deviceNoticeMaps = deviceNoticeMapRepository.findByNoticeId(notice.getNoticeId());
            List<DeviceNoticeDTO> deviceNoticeDTOList = new ArrayList<>();
            for (DeviceNoticeMap deviceNoticeMap : deviceNoticeMaps) {
                Device device = deviceNoticeMap.getDevice();
                DeviceNoticeDTO deviceNoticeDTO = new DeviceNoticeDTO(device.getDeviceId(), device.getDeviceName());
                deviceNoticeDTOList.add(deviceNoticeDTO);
            }
            dto.setDeviceList(deviceNoticeDTOList);

            noticeDTOList.add(dto);
        }

        return noticeDTOList;
    }




    // 공지 상세 조회
    public DetailNoticeDTO getActiveNoticeById(Long noticeId) {
        // 비활성화된 공지를 포함한 조회
        Optional<Notice> noticeOptional = noticeRepository.findByNoticeIdAndIsActiveOrderByRegTimeDesc(noticeId, true);

        // 공지가 비활성화된 경우 예외 처리
        if (noticeOptional.isEmpty()) {
            throw new RuntimeException("해당 공지는 비활성화된 상태입니다."); // 예외 던지기
        }

        Notice notice = noticeOptional.get(); // 공지를 가져옵니다.
        DetailNoticeDTO dto = new DetailNoticeDTO();

        dto.setNoticeId(notice.getNoticeId());
        dto.setAccountId(notice.getAccount() != null ? notice.getAccount().getAccountId() : null);
        dto.setName(notice.getAccount() != null ? notice.getAccount().getName() : null);
        dto.setRole(notice.getAccount() != null ? notice.getAccount().getRole() : null);
        dto.setTitle(notice.getTitle());
        dto.setContent(notice.getContent());
        dto.setRegDate(notice.getRegTime());
        dto.setStartDate(notice.getStartDate());
        dto.setEndDate(notice.getEndDate());


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

        return dto;
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

    @Transactional
    // 공지 비활성화
    public void DeactivationNotice(Long noticeId) {

        // 공지 엔티티 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("해당 공지를 찾을 수 없습니다."));

        // 해당 공지에 대한 디바이스 매핑 삭제
        deviceNoticeMapRepository.deleteByNoticeId(noticeId);

        // 공지 비활성화
        notice.setActive(false);

        // 공지 저장 (변경 사항 반영)
        noticeRepository.save(notice);
    }



    // 공지 수정
    @Transactional
    public void updateNotice(Long noticeId, UpdateNoticeDTO updateNoticeDTO) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다: " + noticeId));

        // 기존 공지 정보 업데이트
        notice.setTitle(updateNoticeDTO.getTitle());
        notice.setContent(updateNoticeDTO.getContent());
        notice.setAccount(notice.getAccount() != null ? notice.getAccount() : null);
        notice.setModifiedBy(notice.getAccount() != null ? notice.getAccount().getName() : null);//작성자 이름
        updateNoticeDTO.setRole(notice.getAccount() != null ? notice.getAccount().getRole() : null);
        notice.setStartDate(updateNoticeDTO.getStartDate());
        notice.setEndDate(updateNoticeDTO.getEndDate());

        // 공지 저장
        noticeRepository.save(notice);

        // 기존 디바이스 매핑 삭제
        deviceNoticeMapRepository.deleteByNoticeId(noticeId);

        // 새 디바이스 매핑 저장
        saveDeviceNoticeMaps(updateNoticeDTO.getDeviceIds(), notice);
    }

}