package com.boot.ksis.service.signage;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.account.AccountDeviceDTO;
import com.boot.ksis.dto.pc.DeviceListDTO;
import com.boot.ksis.dto.playlist.*;
import com.boot.ksis.dto.signage.*;
import com.boot.ksis.entity.*;
import com.boot.ksis.entity.MapsId.AccountDeviceMap;
import com.boot.ksis.entity.MapsId.DeviceEncodeMap;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import com.boot.ksis.entity.MapsId.PlaylistSequence;
import com.boot.ksis.repository.account.AccountDeviceMapRepository;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.playlist.PlayListRepository;
import com.boot.ksis.repository.playlist.PlaylistSequenceRepository;
import com.boot.ksis.repository.signage.*;
import com.boot.ksis.repository.upload.EncodedResourceRepository;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SignageService {
    private final SignageRepository signageRepository;
    private final AccountRepository accountRepository;
    private final AccountDeviceMapRepository accountDeviceMapRepository;
    private final DeviceNoticeRepository deviceNoticeRepository;
    private final DeviceEncodeRepository deviceEncodeRepository;
    private final ThumbNailRepository thumbNailRepository;
    private final PlayListRepository playListRepository;
    private final PlaylistSequenceRepository playlistSequenceRepository;
    private final EncodedResourceRepository encodedResourceRepository;
    private final DeviceEncodeMapRepository deviceEncodeMapRepository;
    private final OriginalResourceRepository originalResourceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    //담당자로 등록된 재생장치 목록 조회
    public List<DeviceListDTO> getSignageList(String accountId){
        //계정-디바이스 맵핑 테이블에서 계정아이디로 디바이스 목록을 가져옴
        List<AccountDeviceMap> accountDeviceMapList = accountDeviceMapRepository.findByAccountId(accountId);

        List<Device> deviceList = new ArrayList<>();

        //가져온 재생장치 목록 중 SIGNAGE에 해당하는 디바이스만 추가
        for(AccountDeviceMap accountDeviceMap : accountDeviceMapList){
            Device device = accountDeviceMap.getDevice();
            if(device.getDeviceType() == DeviceType.SIGNAGE){
                deviceList.add(device);
            }
        }

        //해당 디바이스의 정보를 DTO에 담아서 return
        return deviceList.stream().map(device -> {
            //계정-디바이스 맵핑 테이블에서 디바이스아이디로 해당 디바이스의 담당자들을 가져옴
            List<AccountDeviceDTO> accountDTOList = accountDeviceMapRepository.findByDeviceId(device.getDeviceId())
                    .stream()
                    .map(map -> {
                        Account account = map.getAccount();
                        return new AccountDeviceDTO(account.getAccountId(), account.getName());
                    })
                    .collect(Collectors.toList());

            return new DeviceListDTO(device.getDeviceId(), device.getDeviceName(), accountDTOList, device.getRegTime());
        }).collect(Collectors.toList());
    }

    //모든 재생장치 조회
    public List<DeviceListDTO> getSignageAll(){
        //디바이스 목록에서 SIGNAGE만 조회
        List<Device> deviceList = signageRepository.findByDeviceType(DeviceType.SIGNAGE);

        //해당 디바이스의 정보를 DTO에 담아서 return
        return deviceList.stream().map(device -> {
            //계정-디바이스 맵핑 테이블에서 디바이스아이디로 해당 디바이스의 담당자들을 가져옴
            List<AccountDeviceDTO> accountDTOList = accountDeviceMapRepository.findByDeviceId(device.getDeviceId())
                    .stream()
                    .map(map -> {
                        Account account = map.getAccount();
                        return new AccountDeviceDTO(account.getAccountId(), account.getName());
                    })
                    .collect(Collectors.toList());

            return new DeviceListDTO(device.getDeviceId(), device.getDeviceName(), accountDTOList, device.getRegTime());
        }).collect(Collectors.toList());
    }

    //담당자로 등록된 재생장치 목록 조회
    public List<SignageGridDTO> getSignageGridList(String accountId){
        //계정-디바이스 맵핑 테이블에서 계정아이디로 디바이스 목록을 가져옴
        List<AccountDeviceMap> accountDeviceMapList = accountDeviceMapRepository.findByAccountId(accountId);

        List<Device> deviceList = new ArrayList<>();

        //가져온 재생장치 목록 중 SIGNAGE에 해당하는 디바이스만 추가
        for(AccountDeviceMap accountDeviceMap : accountDeviceMapList){
            Device device = accountDeviceMap.getDevice();
            if(device.getDeviceType() == DeviceType.SIGNAGE){
                deviceList.add(device);
            }
        }

        List<SignageGridDTO> signageGridDTOList = new ArrayList<>();

        for(Device device : deviceList){
            //디바이스에 선택된 재생목록 가져오기
            PlayList playList = playListRepository.findByDeviceAndIsDefault(device, true);

            String thumbNailPath;

            if(playList != null){
                //재생목록에서 첫 번째 파일 가져오기
                PlaylistSequence playlistSequence = playlistSequenceRepository.findByPlaylistIdAndSequence(playList.getPlaylistId(), 1);

                //해당 파일의 썸네일 경로 가져오기
                ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(playlistSequence.getEncodedResource().getOriginalResource());
                thumbNailPath = thumbNail.getFilePath();
            }else {
                thumbNailPath = device.getDeviceName();
            }
            //DTO에 정보 담아서 return
            SignageGridDTO signageGridDTO = SignageGridDTO.builder().deviceId(device.getDeviceId()).deviceName(device.getDeviceName()).thumbNail(thumbNailPath).build();

            signageGridDTOList.add(signageGridDTO);
        }
        return signageGridDTOList;
    }

    //모든 재생장치 조회
    public List<SignageGridDTO> getSignageGridAll(){
        //디바이스 목록에서 SIGNAGE만 조회
        List<Device> deviceList = signageRepository.findByDeviceType(DeviceType.SIGNAGE);

        List<SignageGridDTO> signageGridDTOList = new ArrayList<>();

        for(Device device : deviceList){
            //디바이스에 선택된 재생목록 가져오기
            PlayList playList = playListRepository.findByDeviceAndIsDefault(device, true);

            String thumbNailPath;

            if(playList != null){
                //재생목록에서 첫 번째 파일 가져오기
                PlaylistSequence playlistSequence = playlistSequenceRepository.findByPlaylistIdAndSequence(playList.getPlaylistId(), 1);

                //해당 파일의 썸네일 경로 가져오기
                ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(playlistSequence.getEncodedResource().getOriginalResource());
                thumbNailPath = thumbNail.getFilePath();
            }else {
                thumbNailPath = device.getDeviceName();
            }
            //DTO에 정보 담아서 return
            SignageGridDTO signageGridDTO = SignageGridDTO.builder().deviceId(device.getDeviceId()).deviceName(device.getDeviceName()).thumbNail(thumbNailPath).build();

            signageGridDTOList.add(signageGridDTO);
        }
        return signageGridDTOList;
    }

    //재생장치 등록
    public void saveNewSignage(SignageFormDTO signageFormDTO, List<String> accountList){
        Device device = signageFormDTO.createNewSignage();

        signageRepository.save(device);

        //담당자로 선택한 계정 목록을 계정-디바이스 맵핑테이블에 추가
        for (String accountId : accountList) {
            Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

            AccountDeviceMap accountDeviceMap = new AccountDeviceMap();
            accountDeviceMap.setDeviceId(signageFormDTO.getDeviceId());
            accountDeviceMap.setAccountId(accountId);

            accountDeviceMap.setAccount(account);
            accountDeviceMap.setDevice(device);

            accountDeviceMapRepository.save(accountDeviceMap);
        }
    }

    //재생장치 등록 시 MAC 주소 중복 검증
    public boolean checkMacAddress(SignageFormDTO signageFormDTO){
        Device device = signageRepository.findByMacAddress(signageFormDTO.getMacAddress());

        return device == null;
    }

    //재생장치 수정 시 MAC 주소 중복 검증(기존 MAC 주소는 제외시키기)
    public boolean checkUpdateMacAddress(SignageFormDTO signageFormDTO){
        Device device = signageRepository.findById(signageFormDTO.getDeviceId()).orElseThrow();

        Device checkDevice = signageRepository.findByMacAddress(signageFormDTO.getMacAddress());

        if(checkDevice == null){
            return true;
        }else return Objects.equals(device.getMacAddress(), signageFormDTO.getMacAddress());
    }

    //재생장치 수정
    public void updateSignage(SignageFormDTO signageFormDTO, List<String> accountList){
        //디바이스 아이디로 디바이스 조회 후 업데이트
        Device device = signageRepository.findById(signageFormDTO.getDeviceId()).orElseThrow(EntityNotFoundException::new);
        device.updateSignage(signageFormDTO);

        //기존 담당자 목록 삭제
        accountDeviceMapRepository.deleteByDeviceId(device.getDeviceId());

        //담당자로 선택한 계정 목록을 계정-디바이스 맵핑테이블에 추가
        for (String accountId : accountList) {
            Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

            AccountDeviceMap accountDeviceMap = new AccountDeviceMap();
            accountDeviceMap.setDeviceId(signageFormDTO.getDeviceId());
            accountDeviceMap.setAccountId(accountId);

            accountDeviceMap.setAccount(account);
            accountDeviceMap.setDevice(device);

            accountDeviceMapRepository.save(accountDeviceMap);
        }

        signageRepository.save(device);
    }

    //재생장치 기존 정보 조회
    @Transactional
    public SignageFormDTO getSignageDtl(Long signageId){
        //디바이스 아이디로 디바이스 조회
        Device device = signageRepository.findById(signageId).orElseThrow(EntityNotFoundException::new);

        //계정-디바이스 맵핑 테이블에서 디바이스 아이디로 담당자 목록 조회
        List<AccountDeviceDTO> accountDTOList = accountDeviceMapRepository.findByDeviceId(device.getDeviceId())
                .stream()
                .map(map -> {
                    Account account = map.getAccount();
                    return new AccountDeviceDTO(account.getAccountId(), account.getName());
                })
                .collect(Collectors.toList());

        return SignageFormDTO.of(device, accountDTOList);
    }

    //재생장치 공지 표시 여부 수정
    @Transactional
    public void updateSignageStatus(Long signageId, boolean isShow) {
        Device device = signageRepository.findById(signageId)
                .orElseThrow(() -> new RuntimeException("Signage not found"));
        device.setIsShow(isShow);
        signageRepository.save(device);
    }

    //재생장치 공지 목록 조회
    public List<SignageNoticeDTO> getSignageNotice(Long signageId){
        List<SignageNoticeDTO> signageNoticeDTOList = new ArrayList<>();

        //디바이스-공지 맵핑 테이블에서 디바이스 아이디가 있는 공지 목록 조회
        List<DeviceNoticeMap> deviceNoticeMaps = deviceNoticeRepository.findByDeviceId(signageId);


        for (DeviceNoticeMap deviceNoticeMap : deviceNoticeMaps) {
            Notice notice = deviceNoticeMap.getNotice();

            //공지 작성자 아이디, 이름
            AccountDeviceDTO accountDeviceDTO = new AccountDeviceDTO(notice.getAccount().getAccountId(), notice.getAccount().getName());

            //공지 목록을 DTO에 담아서 return
            SignageNoticeDTO signageNoticeDTO = new SignageNoticeDTO(notice.getNoticeId(), notice.getTitle(), accountDeviceDTO, notice.getRegTime(), notice.getStartDate(), notice.getEndDate());

            signageNoticeDTOList.add(signageNoticeDTO);
        }
        return signageNoticeDTOList;
    }

    //재생장치에 등록된 파일 조회
    public List<SignageResourceDTO> getResourceList(Long signageId){
        List<SignageResourceDTO> signageResourceDTOList = new ArrayList<>();

        //디바이스-인코딩 맵핑 테이블에서 디바이스 아이디로 인코딩 resource 목록 조회
        List<DeviceEncodeMap> deviceEncodeMaps = deviceEncodeRepository.findByDeviceId(signageId);

        for(DeviceEncodeMap deviceEncodeMap : deviceEncodeMaps){
            EncodedResource encodedResource = deviceEncodeMap.getEncodedResource();

            //인코딩 resource의 썸네일 경로
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(encodedResource.getOriginalResource());

            //인코딩 resource 목록을 DTO에 담아서 return
            SignageResourceDTO signageResourceDTO = new SignageResourceDTO(encodedResource.getEncodedResourceId(), encodedResource.getFileTitle(), thumbNail.getFilePath());

            signageResourceDTOList.add(signageResourceDTO);
        }
        return signageResourceDTOList;
    }

    //본인이 업로드한 파일 조회
    public List<SignageResourceDTO> getAccountResourceList(String accountId){
        List<SignageResourceDTO> signageResourceDTOList = new ArrayList<>();

        Account account = accountRepository.findByAccountId(accountId).orElse(null);

        if(account != null){
            //계정으로 업로드한 원본 resource 목록 조회
            List<OriginalResource> originalResourceList = originalResourceRepository.findByAccount(account);

            //원본 resource로 인코딩 resource 목록 조회
            for(OriginalResource originalResource : originalResourceList){
                List<EncodedResource> encodedResourceList = encodedResourceRepository.findByOriginalResource(originalResource);

                //인코딩 resource 썸네일 경로
                for(EncodedResource encodedResource : encodedResourceList){
                    ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(encodedResource.getOriginalResource());

                    //인코딩 resource 목록을 DTO에 담아서 return
                    SignageResourceDTO signageResourceDTO = new SignageResourceDTO(encodedResource.getEncodedResourceId(), encodedResource.getFileTitle(), thumbNail.getFilePath());

                    signageResourceDTOList.add(signageResourceDTO);
                }
            }
        }
        return signageResourceDTOList;
    }

    //재생장치에 resource 추가
    public void addSignageResource(Long signageId, List<Long> encodedResourceIdList){
        //디바이스 아이디롤 디바이스 조회
        Device device = signageRepository.findByDeviceId(signageId);

        //추가한 인코딩 resource를 디바이스-인코딩 맵핑 테이블에 추가
        for(Long encodedResourceId : encodedResourceIdList){
            EncodedResource encodedResource = encodedResourceRepository.findByEncodedResourceId(encodedResourceId);

            DeviceEncodeMap deviceEncodeMap = new DeviceEncodeMap();
            deviceEncodeMap.setEncodedResourceId(encodedResourceId);
            deviceEncodeMap.setDeviceId(signageId);
            deviceEncodeMap.setDevice(device);
            deviceEncodeMap.setEncodedResource(encodedResource);

            deviceEncodeMapRepository.save(deviceEncodeMap);
        }
    }

    //재생장치에 등록된 resource 삭제
    public void deleteEncodedResource(Long signageId, Long encodedResourceId){
        //디바이스-인코딩 맵핑 테이블에서 삭제
        deviceEncodeRepository.deleteByDeviceIdAndEncodedResourceId(signageId, encodedResourceId);
    }

    //
    public List<PlayListDTO> getPlaylistList(Long signageId){
        Device device = signageRepository.findByDeviceId(signageId);
        List<PlayList> playLists = playListRepository.findByDevice(device);

        List<PlayListDTO> playListDTOList = new ArrayList<>();

        for(PlayList playList : playLists){
            PlayListDTO playListDTO = new PlayListDTO(playList.getPlaylistId(), playList.getFileTitle(), playList.getRegTime(), playList.getIsDefault(), playList.getSlideTime());

            playListDTOList.add(playListDTO);
        }

        return playListDTOList;
    }

    public void setPlaylist(Long signageId, Long playlistId){
        Device device = signageRepository.findByDeviceId(signageId);
        List<PlayList> playLists = playListRepository.findByDevice(device);

        for(PlayList playList : playLists){
            playList.setIsDefault(Objects.equals(playList.getPlaylistId(), playlistId));

            playListRepository.save(playList);
        }
    }

    public List<PlayListDtlDTO> getPlaylistDtl(Long playlistId){
        List<PlaylistSequence> playlistSequences = playlistSequenceRepository.findByPlaylistId(playlistId);

        List<PlayListDtlDTO> playListDtlDTOList = new ArrayList<>();

        for(PlaylistSequence playlistSequence : playlistSequences){
            EncodedResource encodedResource = playlistSequence.getEncodedResource();
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(encodedResource.getOriginalResource());

            PlayListDtlDTO playListDtlDTO = new PlayListDtlDTO(encodedResource.getEncodedResourceId(), encodedResource.getFileTitle(), thumbNail.getFilePath(), playlistSequence.getSequence());

            playListDtlDTOList.add(playListDtlDTO);
        }

        playListDtlDTOList.sort(Comparator.comparingInt(PlayListDtlDTO::getSequence));
        return playListDtlDTOList;
    }

    public void deletePlaylist(Long playlistId){
        //재생순서 삭제
        playlistSequenceRepository.deleteByPlaylistId(playlistId);

        //재생목록 삭제
        playListRepository.deleteById(playlistId);
    }

    public void addPlaylist(PlayListAddDTO playListAddDTO, List<PlayListSequenceDTO> playListSequenceDTOList){
        Device device = signageRepository.findByDeviceId(playListAddDTO.getDeviceId());

        PlayList playList = playListRepository.save(playListAddDTO.createNewSignage(device));

        for(PlayListSequenceDTO playListSequenceDTO : playListSequenceDTOList){
            EncodedResource encodedResource = encodedResourceRepository.findByEncodedResourceId(playListSequenceDTO.getEncodedResourceId());

            PlaylistSequence playlistSequence = PlaylistSequence.builder()
                                                                .playlistId(playList.getPlaylistId())
                                                                .playList(playList)
                                                                .encodedResource(encodedResource)
                                                                .sequence(playListSequenceDTO.getSequence())
                                                                .build();

            playlistSequenceRepository.save(playlistSequence);
        }
    }

    public void resourceSequence(Long playListId, PlayListAddDTO playListAddDTO, List<PlayListSequenceDTO> playListSequenceDTOList){
        PlayList playList = playListRepository.findByPlaylistId(playListId);

        playList.updatePlaylist(playListAddDTO);
        playListRepository.save(playList);

        playlistSequenceRepository.deleteByPlaylistId(playListId);

        for(PlayListSequenceDTO playListSequenceDTO : playListSequenceDTOList){
            EncodedResource encodedResource = encodedResourceRepository.findByEncodedResourceId(playListSequenceDTO.getEncodedResourceId());

            PlaylistSequence playlistSequence = PlaylistSequence.builder()
                    .playlistId(playList.getPlaylistId())
                    .playList(playList)
                    .encodedResource(encodedResource)
                    .sequence(playListSequenceDTO.getSequence())
                    .build();

            playlistSequenceRepository.save(playlistSequence);
        }
    }

    public PlayListUpdateDTO playListDtl(Long playListId){
        List<PlaylistSequence> playlistSequenceList = playlistSequenceRepository.findByPlaylistId(playListId);
        playlistSequenceList.sort(Comparator.comparingInt(PlaylistSequence::getSequence));

        List<SignageResourceDTO> signageResourceDTOList = new ArrayList<>();

        for(PlaylistSequence sequence : playlistSequenceList){
            EncodedResource encodedResource = sequence.getEncodedResource();
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(encodedResource.getOriginalResource());
            SignageResourceDTO signageResourceDTO = SignageResourceDTO.builder()
                                                                    .encodedResourceId(encodedResource.getEncodedResourceId())
                                                                    .fileTitle(encodedResource.getFileTitle())
                                                                    .thumbFilePath(thumbNail.getFilePath())
                                                                    .build();

            signageResourceDTOList.add(signageResourceDTO);
        }

        PlayList playList = playListRepository.findByPlaylistId(playListId);

        return PlayListUpdateDTO.builder()
                                .slideTime(playList.getSlideTime())
                                .fileTitle(playList.getFileTitle())
                                .SignageResourceDTO(signageResourceDTOList)
                                .build();
    }

    //재생장치 삭제
    @Transactional
    public void deleteSignage(List<Long> signageIds){
        for(Long signage : signageIds){
            Device device = signageRepository.findByDeviceId(signage);
            List<PlayList> playListList = playListRepository.findByDevice(device);

            for(PlayList playList : playListList){
                //순서 삭제
                playlistSequenceRepository.deleteByPlaylistId(playList.getPlaylistId());
                //재생 목록 삭제
                playListRepository.deleteById(playList.getPlaylistId());
            }
        }
        //인코딩 맵 삭제
        deviceEncodeMapRepository.deleteByDeviceIdIn(signageIds);

        //계정 맵 삭제
        accountDeviceMapRepository.deleteByDeviceIdIn(signageIds);

        //공지 맵 삭제
        deviceNoticeRepository.deleteByDeviceIdIn(signageIds);

        entityManager.flush();

        //재생장치 삭제
        signageRepository.deleteAllByIdInBatch(signageIds);
    }

    //재생목록 재생
    public List<PlayDTO> getPlaylistPlay(Long signageId){
        List<PlayDTO> playDTOList = new ArrayList<>();

        Device device = signageRepository.findByDeviceId(signageId);
        PlayList playList = playListRepository.findByDeviceAndIsDefault(device, true);
        List<PlaylistSequence> playlistSequenceList = playlistSequenceRepository.findByPlaylistId(playList.getPlaylistId());

        for(PlaylistSequence playlistSequence : playlistSequenceList){
            EncodedResource encodedResource = playlistSequence.getEncodedResource();
            float playTime;

            if(encodedResource.getResourceType() == ResourceType.IMAGE){
                playTime = playList.getSlideTime();
            }else {
                playTime = encodedResource.getPlayTime();
            }
            PlayDTO playDTO = PlayDTO.builder()
                                    .playTime(playTime)
                                    .resourceType(encodedResource.getResourceType())
                                    .encodedResourceId(encodedResource.getEncodedResourceId())
                                    .filePath(encodedResource.getFilePath())
                                    .sequence(playlistSequence.getSequence())
                                    .build();

            playDTOList.add(playDTO);
        }
        playDTOList.sort(Comparator.comparingInt(PlayDTO::getSequence));
        return playDTOList;
    }

    //공지 목록 재생
    public List<String> getPlayNotice(Long signageId){
        List<String> notices = new ArrayList<>();
        Device device = signageRepository.findByDeviceId(signageId);

        if(device.getIsShow()){
            List<DeviceNoticeMap> noticeList = deviceNoticeRepository.findByDeviceId(signageId);

            LocalDate nowDate = LocalDate.now();

            for(DeviceNoticeMap deviceNoticeMap : noticeList){
                Notice notice = deviceNoticeMap.getNotice();
                LocalDate startDate = notice.getStartDate();
                LocalDate endDate = notice.getEndDate();

                if((startDate.isBefore(nowDate) || startDate.isEqual(nowDate)) && (endDate.isAfter(nowDate) || endDate.isEqual(nowDate))){
                    String noticeContent = notice.getContent();
                    notices.add(noticeContent);
                }
            }
        }
        return notices;
    }
}
