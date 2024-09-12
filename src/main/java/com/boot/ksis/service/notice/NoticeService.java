package com.boot.ksis.service.notice;

import com.boot.ksis.dto.notice.DeviceListDTO;
import com.boot.ksis.dto.notice.DeviceNoticeDTO;
import com.boot.ksis.dto.notice.DetailNoticeDTO;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    @PersistenceContext
    private EntityManager entityManager;


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



    // 공지 조회 (전체)
    public List<DeviceListDTO> getAllNotices() {
        List<Notice> notices = noticeRepository.findAll();
        List<DeviceListDTO> noticeDTOList = new ArrayList<>();


        for (Notice notice : notices) {
            DeviceListDTO dto = new DeviceListDTO();
            dto.setNoticeId(notice.getNoticeId());
            dto.setAccountId(notice.getAccount() != null ? notice.getAccount().getAccountId() : null);
            dto.setName(notice.getAccount() != null ? notice.getAccount().getName() : null);
            dto.setRole(notice.getAccount()!=null ? notice.getAccount().getRole() : null);
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
    public DetailNoticeDTO getNoticeById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElse(null);
        DetailNoticeDTO dto = new DetailNoticeDTO();
            if (notice != null) {
                dto.setNoticeId(notice.getNoticeId());
                dto.setAccountId(notice.getAccount() != null ? notice.getAccount().getAccountId() : null);
                dto.setName(notice.getAccount() != null ? notice.getAccount().getName() : null);
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
            }

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
    // 공지 삭제
    public void deleteNotice(Long noticeId) {
        // 해당 공지에 대한 디바이스 매핑 삭제
        deviceNoticeMapRepository.deleteByNoticeId(noticeId);

        // 공지 삭제
        noticeRepository.deleteByNoticeId(noticeId);
    }


    // 공지 수정
    @Transactional
    public void updateNotice(Long noticeId, NoticeDTO noticeDTO) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다: " + noticeId));

        // 기존 공지 정보 업데이트
        Account account = accountRepository.findByAccountId(noticeDTO.getAccountId()).orElse(null);
        notice.setTitle(noticeDTO.getTitle());
        notice.setContent(noticeDTO.getContent());
        notice.setAccount(account);
        notice.setStartDate(noticeDTO.getStartDate());
        notice.setEndDate(noticeDTO.getEndDate());

        // 공지 저장
        noticeRepository.save(notice);

        // 기존 디바이스 매핑 삭제
        deviceNoticeMapRepository.deleteByNoticeId(noticeId);

        // 새 디바이스 매핑 저장
        saveDeviceNoticeMaps(noticeDTO.getDeviceIds(), notice);
    }

}