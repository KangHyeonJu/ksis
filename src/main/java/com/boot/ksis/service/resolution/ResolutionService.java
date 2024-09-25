package com.boot.ksis.service.resolution;

import com.boot.ksis.dto.resolution.ResolutionDTO;
import com.boot.ksis.entity.Resolution;
import com.boot.ksis.repository.resolution.ResolutionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResolutionService {
    private final ResolutionRepository resolutionRepository;

    public List<ResolutionDTO> getResolutionList(){
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
}
