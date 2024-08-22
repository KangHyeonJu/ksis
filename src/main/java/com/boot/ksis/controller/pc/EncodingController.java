package com.boot.ksis.controller.pc;

import com.boot.ksis.dto.EncodingRequestDTO;
import com.boot.ksis.service.upload.EncodedResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EncodingController {

    private final EncodedResourceService encodedResourceService;

    @PostMapping("/encoding")
    public ResponseEntity<String> startEncoding(@RequestBody Map<String, EncodingRequestDTO> encodings) {
        // 각 파일에 대한 인코딩 설정 및 제목을 출력
        encodings.forEach((fileName, request) -> {
            System.out.println("File: " + fileName);
            System.out.println("  Title: " + request.getTitle());
            request.getEncodings().forEach(encoding -> {
                System.out.println("  Format: " + encoding.get("format"));
                System.out.println("  Resolution: " + encoding.get("resolution"));
            });
        });

        // 각 파일에 대한 인코딩 정보를 데이터베이스에 저장
        encodings.forEach((fileName, request) -> {
            request.getEncodings().forEach(encoding -> {
                // 인코딩 정보 저장
                encodedResourceService.saveEncodingInfo(fileName, request.getTitle(), encoding.get("format"), encoding.get("resolution"));
            });
        });

        // 인코딩 서비스 호출
        encodedResourceService.startEncoding(encodings);

        return ResponseEntity.ok("Encoding process started.");
    }
}
