package com.boot.ksis.service.file;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileTypeService {

    // Apache Tika 인스턴스 생성
    private final Tika tika = new Tika();

    // 이미지 및 비디오 파일의 MIME 타입 그룹
    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/", "video/");

    public Map<String, Object> validateFileTypes(List<MultipartFile> files) {
        List<String> invalidFiles = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();

        for (MultipartFile file : files) {
            try {
                // Apache Tika를 사용하여 파일의 MIME 타입 추출
                String mimeType = tika.detect(file.getInputStream());

                // MIME 타입이 'image/' 또는 'video/'로 시작하는지 확인
                if (mimeType == null || ALLOWED_MIME_TYPES.stream().noneMatch(mimeType::startsWith)) {
                    invalidFiles.add(file.getOriginalFilename());
                }
            } catch (IOException e) {
                invalidFiles.add(file.getOriginalFilename()); // 오류가 발생한 파일도 비정상 파일로 처리
            }
        }

        // 유효하지 않은 파일이 있는 경우에만 'invalidFiles'를 응답에 포함
        if (!invalidFiles.isEmpty()) {
            response.put("invalidFiles", invalidFiles);
        }

        return response;
    }
}
