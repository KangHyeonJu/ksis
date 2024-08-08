package com.boot.ksis.service;

import com.boot.ksis.entity.API;
import com.boot.ksis.repository.APIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class APIService {

    @Autowired
    private APIRepository apiRepository;

    // API 저장
    public API saveAPI(API api) {
        return apiRepository.save(api);
    }

    // 모든 API 조회
    public List<API> getAllAPIs() {
        return apiRepository.findAll();
    }
}
