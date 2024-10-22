package com.boot.ksis.controller.api;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.api.ApiDTO;
import com.boot.ksis.service.api.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;

    // API 등록
    @CustomAnnotation(activityDetail = "API 등록")
    @PostMapping("/register")
    public ResponseEntity<ApiDTO> registerAPI(@RequestBody ApiDTO apiDTO) {
        ApiDTO savedApiDTO = apiService.saveApi(apiDTO);
        return ResponseEntity.ok(savedApiDTO);
    }

    // API 수정
    @CustomAnnotation(activityDetail = "API 수정")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiDTO> updateAPI(@PathVariable Long id, @RequestBody ApiDTO apiDTO) {
        ApiDTO updatedApiDTO = apiService.updateApi(id, apiDTO);
        if (updatedApiDTO != null) {
            return ResponseEntity.ok(updatedApiDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // API 삭제
    @CustomAnnotation(activityDetail = "API 삭제")
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deleteAPI(@PathVariable Long id) {
        apiService.deleteApi(id);
        return ResponseEntity.noContent().build();
    }

    // 모든 API 조회
    @GetMapping("/all")
    public ResponseEntity<Page<ApiDTO>> getAllAPIs(@RequestParam int page,
                                                   @RequestParam int size,
                                                   @RequestParam(required = false) String searchTerm,
                                                   @RequestParam(required = false) String searchCategory) {
        Page<ApiDTO> apis = apiService.getAllApis(page, size, searchTerm, searchCategory);
        return ResponseEntity.ok(apis);
    }

    // 특정 API 조회
    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiDTO> getAPIById(@PathVariable Long id) {
        ApiDTO apiDTO = apiService.getApiById(id);
        if (apiDTO != null) {
            return ResponseEntity.ok(apiDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
