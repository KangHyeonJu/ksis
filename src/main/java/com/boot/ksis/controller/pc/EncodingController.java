package com.boot.ksis.controller.pc;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.upload.EncodingRequestDTO;
import com.boot.ksis.service.upload.EncodedResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EncodingController {
    private final EncodedResourceService encodedResourceService;

    @CustomAnnotation(activityDetail = "파일 인코딩")

    @PostMapping("/encoding/{accountId}")
    public ResponseEntity<String> startEncoding(@RequestBody Map<String, EncodingRequestDTO> encodings, @PathVariable("accountId") String accountId) {

        // 인코딩 메타데이터 저장
        encodedResourceService.saveEncodingInfo(encodings);

        // 인코딩 서비스 호출
        encodedResourceService.startEncoding(encodings);

        return ResponseEntity.ok("Encoding process started.");
    }
}
