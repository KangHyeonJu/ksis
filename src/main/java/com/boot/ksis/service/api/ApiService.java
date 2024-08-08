package com.boot.ksis.service.api;

import com.boot.ksis.entity.API;
import com.boot.ksis.repository.api.ApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiService {

    @Autowired
    private ApiRepository apiRepository;

    // API 저장
    public API saveApi(API api) {
        return apiRepository.save(api);
    }

    // API 삭제
    public void deleteApi(Long id) {
        apiRepository.deleteById(id);
    }

    // 모든 API 조회
    public List<API> getAllApis() {
        return apiRepository.findAll();
    }

    // 특정 API 조회
    public API getApiById(Long apiId) {
        return apiRepository.findById(apiId).orElse(null);
    }

    // API 수정
    public API updateApi(Long apiId, API api) {
        return apiRepository.findById(apiId)
                .map(existingApi -> {
                    existingApi.setApiName(api.getApiName());
                    existingApi.setProvider(api.getProvider());
                    existingApi.setKeyValue(api.getKeyValue());
                    existingApi.setExpiryDate(api.getExpiryDate());
                    existingApi.setPurpose(api.getPurpose());
                    return apiRepository.save(existingApi);
                })
                .orElse(null); // API가 존재하지 않을 경우 null 반환
    }
}
