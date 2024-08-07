package com.boot.ksis.controller;

import com.boot.ksis.entity.API;
import com.boot.ksis.service.APIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // React 애플리케이션의 주소
public class APIController {

    @Autowired
    private APIService apiService;

    // API 등록
    @PostMapping("/register")
    public ResponseEntity<API> registerAPI(@RequestBody API api) {
        API savedAPI = apiService.saveAPI(api);
        return ResponseEntity.ok(savedAPI);
    }

    //API 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deleteAPI(@PathVariable Long id) {
        apiService.deleteAPI(id);
        return ResponseEntity.noContent().build();
    }

    // 모든 API 조회
    @GetMapping("/all")
    public ResponseEntity<List<API>> getAllAPIs() {
        List<API> apis = apiService.getAllAPIs();
        return ResponseEntity.ok(apis);
    }

}
