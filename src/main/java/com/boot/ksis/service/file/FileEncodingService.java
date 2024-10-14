package com.boot.ksis.service.file;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.dto.file.OriginResourceListDTO;
import com.boot.ksis.entity.EncodedResource;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.repository.resolution.ResolutionRepository;
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
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FileEncodingService {

    @Value("/file/encoding/")
    // file/encoding/
    String dbLocation;

    @Value("${uploadLocation}")
    // C:/ksis-file/uploads/
    String uploadLocation;

    @Value("${encodingLocation}")
    // C:/ksis-file/encoding/
    String encodingLocation;

    private final EncodedResourceRepository encodedResourceRepository;
    private final OriginalResourceRepository originalResourceRepository;
    private final FileSizeService fileSizeService;

    // 영상 해상도 스케일 설정
    private String getResolutionScale(OriginResourceListDTO originResourceListDTO) {
        // 공백을 제거한 후 해상도를 분리
        String[] parts = originResourceListDTO.getResolution().replace(" ", "").split("x");
        if (parts.length != 2) {
            return null; // 올바르지 않은 입력 형식
        }

        try {
            int width = Integer.parseInt(parts[0]);
            int height = Integer.parseInt(parts[1]);
            return String.format("%d:%d", width, height);
        } catch (NumberFormatException e) {
            return null; // 숫자 변환 실패
        }
    }

    //이미지 해상도 설정
    private Dimension getResolutionDimensions(OriginResourceListDTO originResourceListDTO) {
        // 공백을 제거한 후 해상도를 분리
        String[] parts = originResourceListDTO.getResolution().replace(" ", "").split("x");
        if (parts.length != 2) {
            return null; // 올바르지 않은 입력 형식
        }

        try {
            int width = Integer.parseInt(parts[0]);
            int height = Integer.parseInt(parts[1]);
            return new Dimension(width, height);
        } catch (NumberFormatException e) {
            return null; // 숫자 변환 실패
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

        // originalResourceId를 사용해 OriginalResource에서 fileName을 가져옴
        Optional<OriginalResource> originalResourceOpt = originalResourceRepository.findById(originalResourceId);
        if (!originalResourceOpt.isPresent()) {
            throw new IllegalArgumentException("원본 리소스를 찾을 수 없습니다: id=" + originalResourceId);
        }

        // OriginalResource 엔티티에서 fileName을 가져옴
        OriginalResource originalResource = originalResourceOpt.get();
        String baseName = originalResource.getFileName();  // OriginalResource에서 fileName 가져오기

        //새로 인코딩할 file의 스토리지 및 db에 저장될 이름.
        String fileName = UUID.randomUUID() + "_" + originResourceListDTO.getResolution() + "." + originResourceListDTO.getFormat();

        // 원본 경로
        String inputFilePath = uploadLocation + baseName;

        // 출력 파일 이름 설정
        String outputFileName = encodingLocation  + fileName;

        // 이미지 해상도 설정
        Dimension newSize = getResolutionDimensions(originResourceListDTO);
        System.out.println("인코딩 부분 getResolution : "+originalResource.getResolution());
        if (newSize == null) {
            throw new IllegalArgumentException("잘못된 해상도 형식입니다: " + originResourceListDTO.getResolution());
        }
        String scaleFilter = String.format("scale=%d:%d", newSize.width, newSize.height);

        String command;
        switch (originResourceListDTO.getFormat().toLowerCase()) {
            case "png", "bmp":
                  command = String.format("ffmpeg -i %s -vf %s %s",
                    inputFilePath, scaleFilter, outputFileName);
            break;
            case "jpg":
                command = String.format("ffmpeg -i %s -vf %s -q:v 2 %s",  // -q:v 옵션은 JPG 품질 설정
                        inputFilePath,  scaleFilter, outputFileName);
                break;
            default:
                throw new IOException("지원되지 않는 이미지 형식: " + originResourceListDTO.getFormat());
        }

        Process process = Runtime.getRuntime().exec(command);
        boolean completed;

        try {
            // 1시간 동안 프로세스가 완료되기를 기다림
            completed = process.waitFor(1, TimeUnit.HOURS);
            if (!completed) {
                // 타임아웃 발생시 프로세스를 강제 종료
                process.destroyForcibly();
                throw new IOException("인코딩 실패: 1시간이 초과되었습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("인코딩이 중단되었습니다.", e);
        }
        System.out.println("Image encoded: " + outputFileName);

        // EncodedResource 엔티티 생성 및 저장
        EncodedResource encodedResource = new EncodedResource();


        //db저장 주소 지정
        String filePath =  dbLocation  + fileName;

        encodedResource.setOriginalResource(originalResource);
        encodedResource.setFilePath(filePath);
        encodedResource.setFileName(fileName);
        encodedResource.setFileTitle(originalResource.getFileTitle()+ "_" +originResourceListDTO.getResolution() + "_" + originResourceListDTO.getFormat());
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

                fileSizeService.updateTotalFileSize(encoded);
            } else {
                throw new IllegalArgumentException("파일 이름을 찾을 수 없습니다: " + outputFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 영상 인코딩
    public void videoEncodingBoard(Long originalResourceId, OriginResourceListDTO originResourceListDTO) throws IOException {

        // originalResourceId를 사용해 OriginalResource에서 fileName을 가져옴
        Optional<OriginalResource> originalResourceOpt = originalResourceRepository.findById(originalResourceId);
        if (!originalResourceOpt.isPresent()) {
            throw new IllegalArgumentException("원본 리소스를 찾을 수 없습니다: originalResourceId=" + originalResourceId);
        }


        // OriginalResource 엔티티에서 fileName을 가져옴
        OriginalResource originalResource = originalResourceOpt.get();
        String baseName = originalResource.getFileName();  // OriginalResource에서 fileName 가져오기

        String fileName = UUID.randomUUID() + "_" + originResourceListDTO.getResolution() + "." + originResourceListDTO.getFormat();

        // 원본 경로
        String inputFilePath = uploadLocation + baseName;

        // 출력 파일 이름 설정
        String outputFileName = encodingLocation  + fileName;

        // 해상도에 따른 크기 설정
        String scale = getResolutionScale(originResourceListDTO);
        if (scale == null) {
            throw new IllegalArgumentException("잘못된 해상도 형식입니다: " + originResourceListDTO.getResolution());
        }

        // 포맷에 따라 ffmpeg 명령어 구성
        String command;
        switch (originResourceListDTO.getFormat().toLowerCase()) {

            case "mov":
                command = String.format("ffmpeg -i %s -vf scale=%s -c:v libx264 -c:a aac %s",
                        inputFilePath, scale, outputFileName);
                break;
            case "avi":
                command = String.format("ffmpeg -i %s -vf scale=%s -c:v libxvid -c:a libmp3lame %s",
                        inputFilePath, scale, outputFileName);
                break;
            case "mkv":
                command = String.format("ffmpeg -i %s -vf scale=%s -c:v libx264 -c:a aac %s",
                        inputFilePath, scale, outputFileName);
                break;
            default:
                // 기본적으로 mp4로 인코딩
                command = String.format("ffmpeg -i %s -vf scale=%s -c:v libx264 -c:a aac %s",
                        inputFilePath, scale, outputFileName);
                break;
        }

        Process process = Runtime.getRuntime().exec(command);

        // 표준 출력 및 에러 스트림 처리
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println(line);  // 에러 스트림을 표준 에러 출력으로 출력
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        boolean completed;
        try {
            // 1시간 동안 프로세스가 완료되기를 기다림
            completed = process.waitFor(1, TimeUnit.HOURS);
            if (!completed) {
                // 타임아웃 발생시 프로세스를 강제 종료
                process.destroyForcibly();
                throw new IOException("인코딩 실패: 1시간이 초과되었습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("인코딩이 중단되었습니다.", e);
        }
        System.out.println("Video encoded: " + outputFileName);

        // EncodedResource 엔티티 생성 및 저장
        EncodedResource encodedResource = new EncodedResource();

        //db저장 주소 지정
        String filePath =  dbLocation  + fileName;



        encodedResource.setOriginalResource(originalResource);
        encodedResource.setFilePath(filePath);
        encodedResource.setFileName(fileName);
        encodedResource.setFileTitle(originalResource.getFileTitle()+ "_" +originResourceListDTO.getResolution()
                + "_" + originResourceListDTO.getFormat());
        System.out.println("getFileTitle : "+originalResource.getFileTitle());
        encodedResource.setResolution(originResourceListDTO.getResolution());
        System.out.println("getResolution : "+originalResource.getResolution());
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

                fileSizeService.updateTotalFileSize(encoded);
            } else {
                throw new IllegalArgumentException("파일 이름을 찾을 수 없습니다: " + outputFileName); // EncodedResource가 없을 때
            }
        } catch (IOException e) {
            e.printStackTrace(); // 파일 사이즈 확인 중 오류 발생
        }
    }

}