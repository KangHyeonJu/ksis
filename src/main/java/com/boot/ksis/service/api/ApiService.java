package com.boot.ksis.service.api;

import com.boot.ksis.dto.api.ApiDTO;
import com.boot.ksis.entity.API;
import com.boot.ksis.repository.api.ApiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<ApiDTO> getAllApis() {
        List<API> apis = apiRepository.findAll(Sort.by(Sort.Direction.DESC, "regTime"));
        return apis.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
