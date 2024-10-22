package com.boot.ksis.service.notice;

import com.boot.ksis.constant.Role;
import com.boot.ksis.dto.notice.*;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Device;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import com.boot.ksis.entity.Notice;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.notice.DeviceNoticeMapRepository;
import com.boot.ksis.repository.notice.NoticeRepository;
import com.boot.ksis.repository.signage.DeviceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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
    public Page<NoticeListDTO> getAllActiveNotices(int page, int size, String searchTerm, String searchCategory) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));
        Page<Notice> noticeList;
        if(searchCategory != null && !searchTerm.isEmpty()){
            if(searchCategory.equals("title")){
                noticeList = noticeRepository.findByIsActiveAndTitleContainingIgnoreCase
                        (true, searchTerm, pageable);
            }else if(searchCategory.equals("account")){
                noticeList = noticeRepository.searchByAccountOrNameAndIsActive(searchTerm, true, pageable);
            }else if(searchCategory.equals("regTime")){
                noticeList = noticeRepository.searchByRegTimeContainingIgnoreCaseAndIsActive(searchTerm, true, pageable);
            }else if(searchCategory.equals("device")){
                Page<DeviceNoticeMap> deviceNoticePage = deviceNoticeMapRepository.findByDevice_DeviceNameContainingIgnoreCaseAndNotice_Active(
                        searchTerm, true, pageable);

                // DeviceNoticeMap에서 Notice를 추출하여 Page<Notice>로 변환
                noticeList = deviceNoticePage.map(DeviceNoticeMap::getNotice);
            }else{
                noticeList = noticeRepository.findByIsActive(true, pageable);
            }
        }else{
            noticeList = noticeRepository.findByIsActive(true, pageable);
        }

        // Page<Notice> -> List<Notice>로 변환 후 DTO로 변환
        List<Notice> notices = noticeList.getContent();
        List<NoticeListDTO> noticeDTOList = convertNoticesToDTO(notices);

        // Page<DeviceListDTO>로 변환하여 리턴
        return new PageImpl<>(noticeDTOList, pageable, noticeList.getTotalElements());
    }

    //ADMIN 공지 조회(비활성화 전체)
    public Page<NoticeListDTO> getAllNoneActiveNotices(int page, int size, String searchTerm, String searchCategory) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));
        Page<Notice> noticeList;
        if(searchCategory != null && !searchTerm.isEmpty()){
            if(searchCategory.equals("title")){
                noticeList = noticeRepository.findByIsActiveAndTitleContainingIgnoreCase
                        (false, searchTerm, pageable);
            }else if(searchCategory.equals("account")){
                noticeList = noticeRepository.searchByAccountOrNameAndIsActive(
                        searchTerm, false, pageable);
            }else if(searchCategory.equals("regTime")){
                noticeList = noticeRepository.searchByRegTimeContainingIgnoreCaseAndIsActive(
                        searchTerm, false, pageable);
            }
            else{
                noticeList = noticeRepository.findByIsActive(false, pageable);
            }
        }else{
            noticeList = noticeRepository.findByIsActive(false, pageable);
        }

        // Page<Notice> -> List<Notice>로 변환 후 DTO로 변환
        List<Notice> notices = noticeList.getContent();
        List<NoticeListDTO> noticeDTOList = convertNoticesToDTO(notices);

        // Page<NoticeListDTO>로 변환하여 리턴
        return new PageImpl<>(noticeDTOList, pageable, noticeList.getTotalElements());
    }

    //USER 공지 조회 (활성화 본인 공지)
    public Page<DeviceListDTO> getUserActiveNotices(int page, int size, String searchTerm, String searchCategory, Account accountId) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "noticeId"));

        Page<Notice> noticeList;
        Page<Notice> adminNoticeList;

        if(searchCategory != null && !searchTerm.isEmpty()){
            if(searchCategory.equals("title")){
                noticeList = noticeRepository.findByIsActiveAndAccountAndTitleContainingIgnoreCase
                        (true, accountId, searchTerm, pageable);
                adminNoticeList = noticeRepository.findByAccount_RoleAndIsActiveAndTitleContainingIgnoreCase
                        (Role.ADMIN, true, searchTerm, pageable);
            }else if(searchCategory.equals("regTime")){
                noticeList = noticeRepository.searchByRegTimeContainingIgnoreCaseAndIsActiveAndAccount(
                        searchTerm, true, accountId, pageable);
                adminNoticeList = noticeRepository.searchByRegTimeContainingIgnoreCaseAndAccount_RoleAndIsActive
                        (searchTerm, Role.ADMIN, true, pageable);
            }else if(searchCategory.equals("device")){
                // DeviceNoticeMap에서 Notice를 추출하여 변환
                Page<DeviceNoticeMap> deviceNoticePage = deviceNoticeMapRepository.findByDevice_DeviceNameContainingIgnoreCaseAndNotice_AccountAndNotice_IsActive(
                        searchTerm, accountId, true, pageable);
                Page<DeviceNoticeMap> adminDeviceNoticePage = deviceNoticeMapRepository.findByDevice_DeviceNameContainingIgnoreCaseAndNotice_Account_RoleAndNotice_IsActive(
                        searchTerm, Role.ADMIN, true, pageable);

                // DeviceNoticeMap에서 Notice를 추출하여 Page<Notice>로 변환
                noticeList = deviceNoticePage.map(DeviceNoticeMap::getNotice);
                adminNoticeList = adminDeviceNoticePage.map(DeviceNoticeMap::getNotice);

            }else{
                noticeList = noticeRepository.findByIsActiveAndAccount(true, accountId, pageable);
                adminNoticeList = noticeRepository.findByAccount_RoleAndIsActive
                        (Role.ADMIN, true, pageable);
            }
        }else{
            noticeList = noticeRepository.findByIsActiveAndAccount(true, accountId, pageable);
            adminNoticeList = noticeRepository.findByAccount_RoleAndIsActive
                    (Role.ADMIN, true, pageable);
        }

        // Page<Notice> -> List<Notice>로 변환 후 DTO로 변환
        List<Notice> notices = noticeList.getContent();
        List<Notice> adminNotices = adminNoticeList.getContent();
        List<DeviceListDTO> noticeDTOList = convertUserNoticesToDTO(notices, adminNotices);

        // Page<DeviceListDTO>로 변환하여 리턴
        return new PageImpl<>(noticeDTOList, pageable, noticeList.getTotalElements());
    }

    //USER 공지 조회 (비활성화 본인 공지)
    public Page<NoticeListDTO> getUserNoneActiveNotices(int page, int size, String searchTerm, String searchCategory, Account accountId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));

        Page<Notice> noticeList;

        if(searchCategory != null && !searchTerm.isEmpty()){
            if(searchCategory.equals("title")){
                noticeList = noticeRepository.findByIsActiveAndAccountAndTitleContainingIgnoreCase
                        (false, accountId, searchTerm, pageable);
            }else if(searchCategory.equals("regTime")){
                noticeList = noticeRepository.searchByRegTimeContainingIgnoreCaseAndIsActiveAndAccount(
                        searchTerm, false, accountId, pageable);
            }else{
                noticeList = noticeRepository.findByIsActiveAndAccount(false, accountId, pageable);
            }
        }else{
            noticeList = noticeRepository.findByIsActiveAndAccount(false, accountId, pageable);
        }

        // Page<Notice> -> List<Notice>로 변환 후 DTO로 변환
        List<Notice> notices = noticeList.getContent();
        List<NoticeListDTO> noticeDTOList = convertNoticesToDTO(notices);

        // Page<NoticeListDTO>로 변환하여 리턴
        return new PageImpl<>(noticeDTOList, pageable, noticeList.getTotalElements());
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
    private List<NoticeListDTO> convertNoticesToDTO(List<Notice> notices) {
        List<NoticeListDTO> noticeDTOList = new ArrayList<>();

        for (Notice notice : notices) {
            NoticeListDTO dto = new NoticeListDTO();
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
    public void deactivationNotice(Long noticeId) {

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

    @Transactional
    //비활성화 된 파일 다시 활성화
    public void activationNotice(Long noticeId) {
        // 공지 엔티티 조회
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("해당 공지를 찾을 수 없습니다."));

        notice.setActive(true);
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

    public List<Long> findDeviceNotice(Long noticeId){
        List<DeviceNoticeMap> deviceNoticeMaps = deviceNoticeMapRepository.findByNoticeId(noticeId);
        List<Long> deviceIds = new ArrayList<>();
        for(DeviceNoticeMap deviceNoticeMap : deviceNoticeMaps){
            Long deviceId = deviceNoticeMap.getDeviceId();
            deviceIds.add(deviceId);
        }
        return deviceIds;
    }
}