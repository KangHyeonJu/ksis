package com.boot.ksis.service.file;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.file.EncodeListDTO;
import com.boot.ksis.dto.file.OriginResourceListDTO;
import com.boot.ksis.entity.EncodedResource;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.repository.upload.EncodedResourceRepository;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileEncodingService {

    @Value("${uploadLocation}")
    String uploadLocation;

    @Value("${encodingLocation}")
    String encodingLocation;


    private final EncodedResourceRepository encodedResourceRepository;
    private final OriginalResourceRepository originalResourceRepository;

    // 영상 해상도 스케일 설정
    private String getResolutionScale(String resolution) {
        switch (resolution) {
            case "720p":
                return "1280:720";
            case "1080p":
                return "1920:1080";
            case "4k":
                return "3840:2160";
            default:
                return "640:360"; // Default resolution
        }
    }

    // 이미지 해상도 스케일 설정
    private Dimension getResolutionDimensions(String resolution) {
        switch (resolution) {
            case "720p":
                return new Dimension(1280, 720);
            case "1080p":
                return new Dimension(1920, 1080);
            case "4k":
                return new Dimension(3840, 2160);
            default:
                return new Dimension(640, 360); // Default resolution
        }
    }

    public void imageEncodingBoard(Long originalResourceId, OriginResourceListDTO originResourceListDTO) throws IOException {

        // 파일 제목과 경로 가져오기
        String baseName = originResourceListDTO.getFileTitle();
        String originalFilePath = originResourceListDTO.getFilePath();

        // 파일 경로에서 /file/ 부분 제거
        String filePathWithoutPrefix = originalFilePath.replace("/file/", "");

        // 새로운 파일 경로 생성
        String inputFilePath = "C://ksis-file//" + filePathWithoutPrefix;

        // 출력 파일 이름 설정
        String outputFileName = encodingLocation + baseName + "_" + originResourceListDTO.getResolution() + "." + originResourceListDTO.getFormat();

        // 해상도에 따른 크기 설정
        Dimension newSize = getResolutionDimensions(originResourceListDTO.getResolution());
        String scaleFilter = String.format("scale=%d:%d", newSize.width, newSize.height);

        // 포맷에 따른 명령어 작성
        String[] command;
        switch (originResourceListDTO.getFormat().toLowerCase()) {
            case "png":
                command = new String[]{"ffmpeg", "-i", inputFilePath, "-vf", scaleFilter, outputFileName};
                break;
            case "jpg":
                command = new String[]{"ffmpeg", "-i", inputFilePath, "-vf", scaleFilter, "-q:v", "2", outputFileName};
                break;
            case "bmp":
                command = new String[]{"ffmpeg", "-i", inputFilePath, "-vf", scaleFilter, outputFileName};
                break;
            default:
                throw new IOException("Unsupported image format: " + originResourceListDTO.getFormat());
        }

        // ProcessBuilder를 사용하여 ffmpeg 명령어 실행
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 표준 오류와 표준 출력을 하나로 합침

        try {
            Process process = processBuilder.start();
            // 표준 출력을 읽어오는 부분 (선택적)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // 출력된 내용 로그에 기록
                }
            }

            int exitCode = process.waitFor(); // 프로세스 종료 대기
            if (exitCode == 0) {
                System.out.println("Image encoded: " + outputFileName);
            } else {
                System.err.println("Encoding failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Error during image encoding", e);
        }


        // EncodedResource 엔티티 생성
        EncodedResource encodedResource = new EncodedResource();

        OriginalResource originalResource = originalResourceRepository.findByOriginalResourceId(originalResourceId);
        encodedResource.setOriginalResource(originalResource);
        encodedResource.setFilePath("/file/encoding/" + baseName+"_"+originResourceListDTO.getResolution()+"_"+originResourceListDTO.getFormat());
        encodedResource.setFileName(UUID.randomUUID()+"_"+originResourceListDTO.getResolution()+"."+originResourceListDTO.getFormat());
        encodedResource.setFileTitle(baseName+"_"+originResourceListDTO.getResolution()+"_"+originResourceListDTO.getFormat());
        encodedResource.setResolution(originResourceListDTO.getResolution());
        encodedResource.setFormat(originResourceListDTO.getFormat());
        encodedResource.setRegTime(LocalDateTime.now());
        encodedResource.setResourceType(ResourceType.IMAGE);
        encodedResource.setResourceStatus(ResourceStatus.UPLOADING);
        encodedResource.setFileSize(originResourceListDTO.getFileSize());

        // 데이터베이스에 저장
        encodedResourceRepository.save(encodedResource);

        // 인코딩 완료 로그 출력
        System.out.println("Image encoded and saved to database: " + outputFileName);
    }
}
