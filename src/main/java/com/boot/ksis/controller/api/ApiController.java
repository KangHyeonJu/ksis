package com.boot.ksis.controller.api;

import com.boot.ksis.entity.API;
import com.boot.ksis.service.api.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // React 애플리케이션의 주소
public class ApiController {

    @Autowired
    private ApiService apiService;

    // API 등록
    @PostMapping("/register")
    public ResponseEntity<API> registerAPI(@RequestBody API api) {
        API savedAPI = apiService.saveApi(api);
        return ResponseEntity.ok(savedAPI);
    }

    // API 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<API> updateAPI(@PathVariable Long id, @RequestBody API api) {
        API updatedAPI = apiService.updateApi(id, api);
        if (updatedAPI != null) {
            return ResponseEntity.ok(updatedAPI);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // API 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deleteAPI(@PathVariable Long id) {
        apiService.deleteApi(id);
        return ResponseEntity.noContent().build();
    }

    // 모든 API 조회
    @GetMapping("/all")
    public ResponseEntity<List<API>> getAllAPIs() {
        List<API> apis = apiService.getAllApis();
        return ResponseEntity.ok(apis);
    }

    // 특정 API 조회
    @GetMapping("/posts/{id}")
    public ResponseEntity<API> getAPIById(@PathVariable Long id) {
        API api = apiService.getApiById(id);
        if (api != null) {
            return ResponseEntity.ok(api);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
