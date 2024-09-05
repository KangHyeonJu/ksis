package com.boot.ksis.service.file;

import com.boot.ksis.constant.ResourceStatus;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;
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

    private String getResolutionScale(String resolution) {
        switch (resolution) {
            case "720p":
                return "1280:720";
            case "1080p":
                return "1920:1080";
            case "4k":
                return "3840:2160";
            default:
                return "640:360";
        }
    }

    private Dimension getResolutionDimensions(String resolution) {
        switch (resolution) {
            case "720p":
                return new Dimension(1280, 720);
            case "1080p":
                return new Dimension(1920, 1080);
            case "4k":
                return new Dimension(3840, 2160);
            default:
                return new Dimension(640, 360);
        }
    }

    public void imageEncodingBoard(Long originalResourceId, OriginResourceListDTO originResourceListDTO) throws IOException {
        String baseName = originResourceListDTO.getFileTitle();
        String originalFilePath = originResourceListDTO.getFilePath();

        // 파일 경로에서 /file/ 부분 제거
        String filePathWithoutPrefix = originalFilePath.replace("/file/", "");

        // 새로운 파일 경로 생성
        String inputFilePath = "C:" + File.separator + "ksis-file" + File.separator + filePathWithoutPrefix;

        // 출력 파일 이름 설정
        String outputFileName = encodingLocation + File.separator + baseName + "_" + originResourceListDTO.getResolution() + "." + originResourceListDTO.getFormat();

        Dimension newSize = getResolutionDimensions(originResourceListDTO.getResolution());
        String scaleFilter = String.format("scale=%d:%d", newSize.width, newSize.height);

        String[] command;
        switch (originResourceListDTO.getFormat().toLowerCase()) {
            case "png":
            case "bmp":
                command = new String[]{"ffmpeg", "-i", inputFilePath, "-vf", scaleFilter, outputFileName};
                break;
            case "jpg":
                command = new String[]{"ffmpeg", "-i", inputFilePath, "-vf", scaleFilter, "-q:v", "2", outputFileName};
                break;
            default:
                throw new IOException("Unsupported image format: " + originResourceListDTO.getFormat());
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Image encoded: " + outputFileName);
            } else {
                System.err.println("Encoding failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new IOException("Error during image encoding", e);
        }

        // EncodedResource 엔티티 생성 및 저장
        EncodedResource encodedResource = new EncodedResource();
        String fileName = UUID.randomUUID() + "_" + originResourceListDTO.getResolution() + "." + originResourceListDTO.getFormat();
        String filePath = "/file/encoding/" + fileName;

        // Optional 처리로 null 방지
        Optional<OriginalResource> originalResourceOpt = originalResourceRepository.findById(originalResourceId);
        if (!originalResourceOpt.isPresent()) {
            throw new IllegalArgumentException("Original resource not found for id: " + originalResourceId);
        }

        OriginalResource originalResource = originalResourceOpt.get();
        encodedResource.setOriginalResource(originalResource);
        encodedResource.setFilePath(filePath);
        encodedResource.setFileName(fileName);
        encodedResource.setFileTitle(baseName + "_" + originResourceListDTO.getResolution() + "_" + originResourceListDTO.getFormat());
        encodedResource.setResolution(originResourceListDTO.getResolution());
        encodedResource.setFormat(originResourceListDTO.getFormat());
        encodedResource.setRegTime(LocalDateTime.now());
        encodedResource.setResourceType(originalResource.getResourceType());
        encodedResource.setResourceStatus(ResourceStatus.UPLOADING);
        encodedResource.setFileSize(originResourceListDTO.getFileSize());

        encodedResourceRepository.save(encodedResource);

        // 파일 사이즈 확인 및 DB 업데이트
        try {
            long fileSize = getFileSize(outputFileName);
            Optional<EncodedResource> encodedResourceOpt = encodedResourceRepository.findById(encodedResource.getEncodedResourceId());

            if (encodedResourceOpt.isPresent()) {
                EncodedResource encoded = encodedResourceOpt.get();
                encoded.setFileSize((int) fileSize);
                encoded.setResourceStatus(ResourceStatus.COMPLETED);
                encodedResourceRepository.save(encoded);
                System.out.println("이미지 인코딩 + db 저장 완료 파일 이름: " + outputFileName);
            } else {
                throw new IllegalArgumentException("Encoded resource not found for fileName: " + outputFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long getFileSize(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            return Files.size(file.toPath());
        } else {
            throw new IOException("File not found: " + filePath);
        }
    }
}
