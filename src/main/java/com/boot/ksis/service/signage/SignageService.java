package com.boot.ksis.service.signage;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.dto.*;
import com.boot.ksis.entity.*;
import com.boot.ksis.entity.MapsId.AccountDeviceMap;
import com.boot.ksis.entity.MapsId.DeviceEncodeMap;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import com.boot.ksis.entity.MapsId.PlaylistSequence;
import com.boot.ksis.repository.account.AccountDeviceMapRepository;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.signage.*;
import com.boot.ksis.repository.upload.EncodedResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public List<DeviceListDTO> getSignageList(){
        List<Device> deviceList = signageRepository.findByDeviceType(DeviceType.SIGNAGE);

        return deviceList.stream().map(device -> {
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

    public void saveNewSignage(SignageFormDTO signageFormDTO, List<String> accountList){
        Device device = signageFormDTO.createNewSignage();

        signageRepository.save(device);

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

    public void updateSignage(SignageFormDTO signageFormDTO, List<String> accountList){
        Device device = signageRepository.findById(signageFormDTO.getDeviceId()).orElseThrow(EntityNotFoundException::new);
        device.updateSignage(signageFormDTO);

        accountDeviceMapRepository.deleteByDeviceId(device.getDeviceId());

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

    @Transactional
    public SignageFormDTO getSignageDtl(Long signageId){
        Device device = signageRepository.findById(signageId).orElseThrow(EntityNotFoundException::new);

        List<AccountDeviceDTO> accountDTOList = accountDeviceMapRepository.findByDeviceId(device.getDeviceId())
                .stream()
                .map(map -> {
                    Account account = map.getAccount();
                    return new AccountDeviceDTO(account.getAccountId(), account.getName());
                })
                .collect(Collectors.toList());

        return SignageFormDTO.of(device, accountDTOList);
    }

    @Transactional
    public void updateSignageStatus(Long signageId, boolean isShow) {
        Device device = signageRepository.findById(signageId)
                .orElseThrow(() -> new RuntimeException("Signage not found"));
        device.setIsShow(isShow);
        signageRepository.save(device);
    }

    public List<SignageNoticeDTO> getSignageNotice(Long signageId){
        List<SignageNoticeDTO> signageNoticeDTOList = new ArrayList<>();

        List<DeviceNoticeMap> deviceNoticeMaps = deviceNoticeRepository.findByDeviceId(signageId);

        for (DeviceNoticeMap deviceNoticeMap : deviceNoticeMaps) {
            Notice notice = deviceNoticeMap.getNotice();
            AccountDeviceDTO accountDeviceDTO = new AccountDeviceDTO(notice.getAccount().getAccountId(), notice.getAccount().getName());

            SignageNoticeDTO signageNoticeDTO = new SignageNoticeDTO(notice.getNoticeId(), notice.getTitle(), accountDeviceDTO, notice.getRegTime(), notice.getStartDate(), notice.getEndDate());

            signageNoticeDTOList.add(signageNoticeDTO);
        }
        return signageNoticeDTOList;
    }

    public List<SignageResourceDTO> getResourceList(Long signageId){
        List<SignageResourceDTO> signageResourceDTOList = new ArrayList<>();

        List<DeviceEncodeMap> deviceEncodeMaps = deviceEncodeRepository.findByDeviceId(signageId);

        for(DeviceEncodeMap deviceEncodeMap : deviceEncodeMaps){
            EncodedResource encodedResource = deviceEncodeMap.getEncodedResource();

            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(encodedResource.getOriginalResource());
            SignageResourceDTO signageResourceDTO = new SignageResourceDTO(encodedResource.getEncodedResourceId(), encodedResource.getFileTitle(), thumbNail.getFilePath());

            signageResourceDTOList.add(signageResourceDTO);
        }
        return signageResourceDTOList;
    }

    public void deleteEncodedResource(Long signageId, Long encodedResourceId){
        deviceEncodeRepository.deleteByDeviceIdAndEncodedResourceId(signageId, encodedResourceId);
    }

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
}
