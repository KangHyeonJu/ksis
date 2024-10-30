package com.boot.ksis.service.resolution;

import com.boot.ksis.dto.resolution.ResolutionDTO;
import com.boot.ksis.entity.Resolution;
import com.boot.ksis.repository.resolution.ResolutionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ResolutionService {
    private final ResolutionRepository resolutionRepository;

    public Page<ResolutionDTO> getResolutionList(int page, int size, String searchTerm, String searchCategory){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "resolutionId"));

        Page<Resolution> resolutionList;

        if(searchCategory != null && !searchCategory.isEmpty()){
            if(searchCategory.equals("name")){
                resolutionList = resolutionRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
            }else if(searchCategory.equals("width")){
                resolutionList = resolutionRepository.searchByWidth(searchTerm, pageable);
            }else if(searchCategory.equals("height")){
                resolutionList = resolutionRepository.searchByHeight(searchTerm, pageable);
            }else {
                resolutionList = resolutionRepository.findAll(pageable);
            }
        }else {
            resolutionList = resolutionRepository.findAll(pageable);
        }
        List<ResolutionDTO> resolutionDTOList = new ArrayList<>();

        for(Resolution resolution : resolutionList){
            ResolutionDTO resolutionDTO = ResolutionDTO.builder()
                    .resolutionId(resolution.getResolutionId())
                    .name(resolution.getName())
                    .width(resolution.getWidth())
                    .height(resolution.getHeight())
                    .build();

            resolutionDTOList.add(resolutionDTO);
        }
        return new PageImpl<>(resolutionDTOList, pageable, resolutionList.getTotalElements());
    }

    public List<ResolutionDTO> getResolutionAll(){
        List<Resolution> resolutionList = resolutionRepository.findAll();

        List<ResolutionDTO> resolutionDTOList = new ArrayList<>();

        for(Resolution resolution : resolutionList){
            ResolutionDTO resolutionDTO = ResolutionDTO.builder()
                    .resolutionId(resolution.getResolutionId())
                    .name(resolution.getName())
                    .width(resolution.getWidth())
                    .height(resolution.getHeight())
                    .build();

            resolutionDTOList.add(resolutionDTO);
        }
        return resolutionDTOList;
    }


    public void postResolution(ResolutionDTO resolutionDTO){
        Resolution resolution = resolutionDTO.postResolution();

        resolutionRepository.save(resolution);
    }

    @Transactional
    public void deleteResolutionList(List<Long> resolutionIds){
        resolutionRepository.deleteAllByIdInBatch(resolutionIds);
    }

    public ResolutionDTO getResolution(Long resolutionId){
        Resolution resolution = resolutionRepository.findByResolutionId(resolutionId);
        return ResolutionDTO.builder()
                .resolutionId(resolution.getResolutionId())
                .name(resolution.getName())
                .height(resolution.getHeight())
                .width(resolution.getWidth())
                .build();
    }

    public void updateResolution(ResolutionDTO resolutionDTO){
        Resolution resolution = resolutionRepository.findByResolutionId(resolutionDTO.getResolutionId());
        resolution.updateResolution(resolutionDTO);

        resolutionRepository.save(resolution);
    }

    //해상도 등록 중복 검사
    public boolean checkResolution(ResolutionDTO resolutionDTO){
        List<Resolution> resolutionList = resolutionRepository.findByWidth(resolutionDTO.getWidth());

        boolean check = true;

        for(Resolution resolution : resolutionList){
            if(resolution.getHeight() == resolutionDTO.getHeight()){
                check = false;
                break;
            }
        }
        return check;
    }

    //해상도 수정 중복 검사
    public boolean checkUpdateResolution(ResolutionDTO resolutionDTO){
        Resolution updateResolution = resolutionRepository.findByResolutionId(resolutionDTO.getResolutionId());

        List<Resolution> resolutionList = resolutionRepository.findByWidth(resolutionDTO.getWidth());

        boolean check = true;

        for(Resolution resolution : resolutionList){
            if(resolution.getHeight() == resolutionDTO.getHeight()){
                if(!Objects.equals(resolution.getResolutionId(), updateResolution.getResolutionId())){
                    check = false;
                    break;
                }
            }
        }
        return check;
    }

    
}
