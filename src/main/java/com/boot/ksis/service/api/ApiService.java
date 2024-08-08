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
}
