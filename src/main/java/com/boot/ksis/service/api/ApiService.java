package com.boot.ksis.service.api;

import com.boot.ksis.dto.api.ApiDTO;
import com.boot.ksis.entity.API;
import com.boot.ksis.repository.api.ApiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final ApiRepository apiRepository;

    // API 저장
    public ApiDTO saveApi(ApiDTO apiDTO) {
        API api = convertToEntity(apiDTO);
        API savedApi = apiRepository.save(api);
        return convertToDTO(savedApi);
    }

    // API 삭제
    public void deleteApi(Long id) {
        apiRepository.deleteById(id);
    }

    // 모든 API 조회
    public Page<ApiDTO> getAllApis(int page, int size, String searchTerm, String searchCategory) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));

        Page<API> apiList;

        if (searchCategory != null && !searchCategory.isEmpty()) {
            if(searchCategory.equals("apiName")) {
                apiList = apiRepository.findByApiNameContainingIgnoreCase(searchTerm, pageable);
            }else if(searchCategory.equals("provider")) {
                apiList = apiRepository.findByProviderContainingIgnoreCase(searchTerm, pageable);
            }else if(searchCategory.equals("expiryDate")) {
                apiList = apiRepository.findByExpiryDateContainingIgnoreCase(searchTerm, pageable);
            }else {
                apiList = apiRepository.findAll(pageable);
            }
        }else{
            apiList = apiRepository.findAll(pageable);
        }

        List<ApiDTO> apiDTOS =  new ArrayList<>();

        for (API api : apiList){
            ApiDTO apiDTO = convertToDTO(api);

            apiDTOS.add(apiDTO);
        }

        return new PageImpl<>(apiDTOS, pageable, apiList.getTotalElements());
    }

    // 특정 API 조회
    public ApiDTO getApiById(Long apiId) {
        return apiRepository.findById(apiId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    // API 수정
    public ApiDTO updateApi(Long apiId, ApiDTO apiDTO) {
        return apiRepository.findById(apiId)
                .map(existingApi -> {
                    existingApi.setApiName(apiDTO.getApiName());
                    existingApi.setProvider(apiDTO.getProvider());
                    existingApi.setKeyValue(apiDTO.getKeyValue());
                    existingApi.setExpiryDate(apiDTO.getExpiryDate());
                    existingApi.setPurpose(apiDTO.getPurpose());
                    API updatedApi = apiRepository.save(existingApi);
                    return convertToDTO(updatedApi);
                })
                .orElse(null); // API가 존재하지 않을 경우 null 반환
    }

    // DTO를 엔티티로 변환
    private API convertToEntity(ApiDTO apiDTO) {
        return new API(
                apiDTO.getApiId(),
                apiDTO.getApiName(),
                apiDTO.getProvider(),
                apiDTO.getKeyValue(),
                apiDTO.getExpiryDate(),
                apiDTO.getPurpose()
        );
    }

    // 엔티티를 DTO로 변환
    private ApiDTO convertToDTO(API api) {
        return new ApiDTO(
                api.getApiId(),
                api.getApiName(),
                api.getProvider(),
                api.getKeyValue(),
                api.getExpiryDate(),
                api.getPurpose()
        );
    }
}
