package com.boot.ksis.controller.pc;

import com.boot.ksis.service.upload.EncodedResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EncodingController {

    private final EncodedResourceService encodedResourceService;

    @PostMapping("/encoding")
    public ResponseEntity<String> startEncoding(@RequestBody Map<String, List<Map<String, String>>> encodings) {
        // 각 파일에 대한 인코딩 설정을 출력
        encodings.forEach((fileName, encodingList) -> {
            System.out.println("File: " + fileName);
            encodingList.forEach(encoding -> {
                System.out.println("  Format: " + encoding.get("format"));
                System.out.println("  Resolution: " + encoding.get("resolution"));
            });
        });

        // 인코딩 서비스 호출
        encodedResourceService.startEncoding(encodings);

        return ResponseEntity.ok("Encoding process started.");
    }
}
