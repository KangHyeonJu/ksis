package com.boot.ksis.service.file;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.dto.file.OriginResourceListDTO;
import com.boot.ksis.entity.EncodedResource;
import com.boot.ksis.entity.FileSize;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.repository.file.FileSizeRepository;
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

    @Value("${filePath}")
    String dbFilePath;

    private final EncodedResourceRepository encodedResourceRepository;
    private final OriginalResourceRepository originalResourceRepository;
    private final FileSizeRepository fileSizeRepository;

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
                return "640:360";
        }
    }

    //이미지 해상도 설정
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

    private long getFileSize(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            return Files.size(file.toPath());
        } else {
            throw new IOException("파일을 찾을 수 없습니다: " + filePath);
        }
    }

    // 이미지 인코딩
    public void imageEncodingBoard(Long originalResourceId, OriginResourceListDTO originResourceListDTO) throws IOException {
        String baseName = originResourceListDTO.getFileTitle();
        String originalFilePath = originResourceListDTO.getFilePath();

        // 파일 경로에서 /file/ 부분 제거
        String filePathWithoutPrefix = originalFilePath.replace(dbFilePath + "/", "");

        // 새로운 파일 경로 생성
        String inputFilePath = encodingLocation + filePathWithoutPrefix;

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
                throw new IOException("지원되지 않는 이미지 형식: " + originResourceListDTO.getFormat());
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // 출력된 내용 로그에 기록
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("이미지 인코딩 완료: " + outputFileName); // 로그 메시지 수정
            } else {
                System.err.println("인코딩 실패, 종료 코드: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new IOException("이미지 인코딩 중 오류 발생", e); // 오류 메시지 수정
        }

        // EncodedResource 엔티티 생성 및 저장
        EncodedResource encodedResource = new EncodedResource();
        String fileName = UUID.randomUUID() + "_" + originResourceListDTO.getResolution() + "." + originResourceListDTO.getFormat();
        String filePath = dbFilePath + "/encoding/" + fileName;

        // Optional 처리로 null 방지
        Optional<OriginalResource> originalResourceOpt = originalResourceRepository.findById(originalResourceId);
        if (!originalResourceOpt.isPresent()) {
            throw new IllegalArgumentException("원본 리소스를 찾을 수 없습니다: id=" + originalResourceId);
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
                System.out.println("이미지 인코딩 및 DB 저장 완료, 파일 이름: " + outputFileName);

                //인코딩 용량 추가
                FileSize addFileSize = fileSizeRepository.findById(1).orElseGet(() -> {
                    // 설정이 없으면 기본값으로 새로운 설정 생성
                    FileSize defaultFileSize = new FileSize();
                    defaultFileSize.setTotalVideo(0L);
                    defaultFileSize.setTotalImage(0L);
                    return fileSizeRepository.save(defaultFileSize);
                });
                addFileSize.setTotalImage(addFileSize.getTotalImage() + encodedResource.getFileSize());

            } else {
                throw new IllegalArgumentException("파일 이름을 찾을 수 없습니다: " + outputFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 영상 인코딩
    public void videoEncodingBoard(Long originalResourceId, OriginResourceListDTO originResourceListDTO) throws IOException {
        String baseName = originResourceListDTO.getFileTitle(); // 파일 제목 가져오기
        String originalFilePath = originResourceListDTO.getFilePath(); // 원본 파일 경로 가져오기

        // 파일 경로에서 /file/ 부분 제거
        String filePathWithoutPrefix = originalFilePath.replace(dbFilePath + "/", "");

        // 새로운 파일 경로 생성
        String inputFilePath = encodingLocation + filePathWithoutPrefix;

        // 출력 파일 이름 설정
        String outputFileName = encodingLocation + File.separator + baseName + "_" + originResourceListDTO.getResolution() + "." + originResourceListDTO.getFormat();

        // 해상도에 따른 크기 설정
        String scale = getResolutionScale(originResourceListDTO.getResolution());
        String scaleFilter = String.format("scale=%s", scale);

        // 포맷에 따라 ffmpeg 명령어 구성
        String command;
        switch (originResourceListDTO.getFormat().toLowerCase()) {
            case "mov":
                command = String.format("ffmpeg -i %s -vf %s -c:v libx264 -c:a aac %s", inputFilePath, scaleFilter, outputFileName);
                break;
            case "avi":
                command = String.format("ffmpeg -i %s -vf %s -c:v libxvid -c:a libmp3lame %s", inputFilePath, scaleFilter, outputFileName);
                break;
            case "mkv":
                command = String.format("ffmpeg -i %s -vf %s -c:v libx264 -c:a aac %s", inputFilePath, scaleFilter, outputFileName);
                break;
            default:
                // 기본적으로 mp4로 인코딩
                command = String.format("ffmpeg -i %s -vf %s -c:v libx264 -c:a aac %s", inputFilePath, scaleFilter, outputFileName);
                break;
        }

        // ProcessBuilder를 사용하여 ffmpeg 명령어 실행
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.redirectErrorStream(true); // 오류 스트림과 표준 스트림을 병합

        try {
            Process process = processBuilder.start(); // ffmpeg 프로세스 시작
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // 출력된 내용 로그에 기록
                }
            }

            int exitCode = process.waitFor(); // 프로세스 종료 대기
            if (exitCode == 0) {
                System.out.println("영상 인코딩 완료: " + outputFileName); // 성공 메시지
            } else {
                System.err.println("인코딩 실패, 종료 코드: " + exitCode); // 실패 메시지
            }
        } catch (IOException | InterruptedException e) {
            throw new IOException("영상 인코딩 중 오류 발생", e); // 오류 발생 시 메시지
        }

        // EncodedResource 엔티티 생성 및 저장
        EncodedResource encodedResource = new EncodedResource();
        String fileName = UUID.randomUUID() + "_" + originResourceListDTO.getResolution() + "." + originResourceListDTO.getFormat();
        String filePath = dbFilePath + "/encoding/" + fileName;

        // Optional 처리로 null 방지
        Optional<OriginalResource> originalResourceOpt = originalResourceRepository.findById(originalResourceId);
        if (!originalResourceOpt.isPresent()) {
            throw new IllegalArgumentException("원본 리소스를 찾을 수 없습니다: id=" + originalResourceId); // 원본 리소스가 없을 때
        }

        OriginalResource originalResource = originalResourceOpt.get();
        encodedResource.setOriginalResource(originalResource);
        encodedResource.setFilePath(filePath);
        encodedResource.setFileName(fileName);
        encodedResource.setFileTitle(baseName + "_" + originResourceListDTO.getResolution() + "_" + originResourceListDTO.getFormat());
        encodedResource.setResolution(originResourceListDTO.getResolution());
        encodedResource.setFormat(originResourceListDTO.getFormat());
        encodedResource.setPlayTime(originalResource.getPlayTime());
        encodedResource.setRegTime(LocalDateTime.now());
        encodedResource.setResourceType(originalResource.getResourceType());
        encodedResource.setResourceStatus(ResourceStatus.UPLOADING); // 인코딩 진행 중 상태
        encodedResource.setFileSize(originResourceListDTO.getFileSize());

        encodedResourceRepository.save(encodedResource); // EncodedResource 저장

        // 파일 사이즈 확인 및 DB 업데이트
        try {
            long fileSize = getFileSize(outputFileName); // 인코딩된 파일 사이즈 확인
            Optional<EncodedResource> encodedResourceOpt = encodedResourceRepository.findById(encodedResource.getEncodedResourceId());

            if (encodedResourceOpt.isPresent()) {
                EncodedResource encoded = encodedResourceOpt.get();
                encoded.setFileSize((int) fileSize);
                encoded.setResourceStatus(ResourceStatus.COMPLETED); // 인코딩 완료 상태
                encodedResourceRepository.save(encoded); // EncodedResource 업데이트
                System.out.println("영상 인코딩 및 DB 저장 완료, 파일 이름: " + outputFileName);

                //인코딩 용량 추가
                FileSize addFileSize = fileSizeRepository.findById(1).orElseGet(() -> {
                    // 설정이 없으면 기본값으로 새로운 설정 생성
                    FileSize defaultFileSize = new FileSize();
                    defaultFileSize.setTotalVideo(0L);
                    defaultFileSize.setTotalImage(0L);
                    return fileSizeRepository.save(defaultFileSize);
                });
                addFileSize.setTotalVideo(addFileSize.getTotalImage() + encodedResource.getFileSize());
            } else {
                throw new IllegalArgumentException("파일 이름을 찾을 수 없습니다: " + outputFileName); // EncodedResource가 없을 때
            }
        } catch (IOException e) {
            e.printStackTrace(); // 파일 사이즈 확인 중 오류 발생
        }
    }

}