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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    public Page<NoticeListDTO> getAllActiveNotices(int page, int size, String searchTerm, String searchCategory, String startTime, String endTime) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notice> noticeList;

        // 시작시간과 끝시간을 LocalDateTime으로 파싱
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (startTime != null && !startTime.isEmpty()) {
                LocalDate startDate = LocalDate.parse(startTime, formatter);
                startDateTime = startDate.atStartOfDay(); // 00:00:00으로 변환
            }
            if (endTime != null && !endTime.isEmpty()) {
                LocalDate endDate = LocalDate.parse(endTime, formatter);
                endDateTime = endDate.atTime(23, 59, 59); // 23:59:59으로 변환
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다.");
        }

        if (searchCategory != null && !searchTerm.isEmpty()) {
            if (searchCategory.equals("title")) {
                noticeList = noticeRepository.findActiveNoticesWithTitle(searchTerm, pageable);
            } else if (searchCategory.equals("account")) {
                noticeList = noticeRepository.findActiveNoticesWithAccount(searchTerm, pageable);
            } else if (searchCategory.equals("device")) {
                Page<DeviceNoticeMap> deviceNoticePage =
                        deviceNoticeMapRepository.findActiveNoticesWithDevice(searchTerm, pageable);
                // DeviceNoticeMap에서 Notice를 추출하여 Page<Notice>로 변환
                noticeList = deviceNoticePage.map(DeviceNoticeMap::getNotice);
            } else {
                noticeList = noticeRepository.findActiveNoticesWithAccountsOrdered(pageable);
            }
        } else {
            if (searchCategory.equals("regTime")) {
                noticeList = noticeRepository.findActiveNoticesWithinDateRange(startDateTime, endDateTime, pageable);
            } else {
                noticeList = noticeRepository.findActiveNoticesWithAccountsOrdered(pageable);
            }

        }

        // Page<Notice> -> List<Notice>로 변환 후 DTO로 변환
        List<Notice> notices = noticeList.getContent();
        List<NoticeListDTO> noticeDTOList = convertNoticesToDTO(notices);

        // Page<DeviceListDTO>로 변환하여 리턴
        return new PageImpl<>(noticeDTOList, pageable, noticeList.getTotalElements());
    }


    //ADMIN 공지 조회(비활성화 전체)
    public Page<NoticeListDTO> getAllNoneActiveNotices(int page, int size, String searchTerm, String searchCategory, String startTime, String endTime) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notice> noticeList;

        // 시작시간과 끝시간을 LocalDateTime으로 파싱
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (startTime != null && !startTime.isEmpty()) {
                LocalDate startDate = LocalDate.parse(startTime, formatter);
                startDateTime = startDate.atStartOfDay(); // 00:00:00으로 변환
            }
            if (endTime != null && !endTime.isEmpty()) {
                LocalDate endDate = LocalDate.parse(endTime, formatter);
                endDateTime = endDate.atTime(23, 59, 59); // 23:59:59으로 변환
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다.");
        }

        if (searchCategory != null && !searchTerm.isEmpty()) {
            if (searchCategory.equals("title")) {
                noticeList = noticeRepository.findDeActivationNoticesWithTitle(searchTerm, pageable);
            } else if (searchCategory.equals("account")) {
                noticeList = noticeRepository.findDeActivationNoticesWithAccount(searchTerm, pageable);
            }
          else {
                noticeList = noticeRepository.findDeActivationNoticesWithAccountsOrdered(pageable);
            }
        } else {
            if (searchCategory.equals("regTime")) {
                noticeList = noticeRepository.findDeActivationNoticesWithRegTimeAdmin(startDateTime, endDateTime, pageable);
            }else {
                noticeList = noticeRepository.findDeActivationNoticesWithAccountsOrdered(pageable);
            }
        }

        // Page<Notice> -> List<Notice>로 변환 후 DTO로 변환
        List<Notice> notices = noticeList.getContent();
        List<NoticeListDTO> noticeDTOList = convertNoticesToDTO(notices);

        // Page<DeviceListDTO>로 변환하여 리턴
        return new PageImpl<>(noticeDTOList, pageable, noticeList.getTotalElements());
    }

    //USER 공지 조회 (활성화 본인 공지)
    public Page<NoticeListDTO> getUserActiveNotices(int page, int size, String searchTerm, String searchCategory, Account account, String startTime, String endTime) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "noticeId"));

        Page<Notice> noticeList;

        // account 객체에서 accountId 추출
        String accountId = account.getAccountId();

        // 시작시간과 끝시간을 LocalDateTime으로 파싱
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (startTime != null && !startTime.isEmpty()) {
                LocalDate startDate = LocalDate.parse(startTime, formatter);
                startDateTime = startDate.atStartOfDay(); // 00:00:00으로 변환
            }
            if (endTime != null && !endTime.isEmpty()) {
                LocalDate endDate = LocalDate.parse(endTime, formatter);
                endDateTime = endDate.atTime(23, 59, 59); // 23:59:59으로 변환
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다.");
        }

        if(searchCategory != null && !searchTerm.isEmpty()){
            if(searchCategory.equals("title")){
                noticeList = noticeRepository.findUserNoticesByRoleWithTitle
                        (accountId, searchTerm, pageable);
            }else if(searchCategory.equals("device")){
                // DeviceNoticeMap에서 Notice를 추출하여 변환
                Page<DeviceNoticeMap> deviceNoticePage = deviceNoticeMapRepository.findUserNoticesByRoleWithDevice(
                        accountId, searchTerm, pageable);

                // DeviceNoticeMap에서 Notice를 추출하여 Page<Notice>로 변환
                noticeList = deviceNoticePage.map(DeviceNoticeMap::getNotice);

            }else{
                noticeList = noticeRepository.findUserNoticesByRole(String.valueOf(accountId), pageable);
            }
        }else{
            if(searchCategory.equals("regTime")){
                noticeList = noticeRepository.findByDateRangeIsActiveAndAccount(
                        startDateTime, endDateTime, true, account, pageable);
            }else{
                noticeList = noticeRepository.findUserNoticesByRole(String.valueOf(accountId), pageable);
            }
        }

        // Page<Notice> -> List<Notice>로 변환 후 DTO로 변환
        List<Notice> notices = noticeList.getContent();
        List<NoticeListDTO> noticeDTOList = convertNoticesToDTO(notices);

        // Page<DeviceListDTO>로 변환하여 리턴
        return new PageImpl<>(noticeDTOList, pageable, noticeList.getTotalElements());
    }

    //USER 공지 조회 (비활성화 본인 공지)
    public Page<NoticeListDTO> getUserNoneActiveNotices(int page, int size, String searchTerm, String searchCategory, Account accountId, String startTime, String endTime) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));

        Page<Notice> noticeList;

        String accountIdStr = accountId.getAccountId();

        // 시작시간과 끝시간을 LocalDateTime으로 파싱
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (startTime != null && !startTime.isEmpty()) {
                LocalDate startDate = LocalDate.parse(startTime, formatter);
                startDateTime = startDate.atStartOfDay(); // 00:00:00으로 변환
            }
            if (endTime != null && !endTime.isEmpty()) {
                LocalDate endDate = LocalDate.parse(endTime, formatter);
                endDateTime = endDate.atTime(23, 59, 59); // 23:59:59으로 변환
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다.");
        }

        if(searchCategory != null && !searchTerm.isEmpty()){
            if(searchCategory.equals("title")){
                noticeList = noticeRepository.findByIsActiveAndAccountAndTitleContainingIgnoreCase
                        (false, accountId, searchTerm, pageable);
            }else{
                noticeList = noticeRepository.findByIsActiveAndAccount(false, accountId, pageable);
            }
        }else{
            if(searchCategory.equals("regTime")){
                noticeList = noticeRepository.findDeActivationNoticesWithRegTimeUser(
                        startDateTime, endDateTime, accountIdStr, pageable);
            }else {
                noticeList = noticeRepository.findByIsActiveAndAccount(false, accountId, pageable);
            }
        }

        // Page<Notice> -> List<Notice>로 변환 후 DTO로 변환
        List<Notice> notices = noticeList.getContent();
        List<NoticeListDTO> noticeDTOList = convertNoticesToDTO(notices);

        // Page<NoticeListDTO>로 변환하여 리턴
        return new PageImpl<>(noticeDTOList, pageable, noticeList.getTotalElements());
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
    public DetailNoticeDTO getNoticeById(Long noticeId) {
        // 비활성화된 공지를 포함한 조회
        Optional<Notice> noticeOptional = noticeRepository.findByNoticeIdOrderByRegTimeDesc(noticeId);

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