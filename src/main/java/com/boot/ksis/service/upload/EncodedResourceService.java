package com.boot.ksis.service.upload;

import com.boot.ksis.handler.NotificationWebSocketHandler;
import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.upload.EncodingRequestDTO;
import com.boot.ksis.entity.*;
import com.boot.ksis.entity.Log.UploadLog;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.log.UploadLogRepository;
import com.boot.ksis.repository.notification.NotificationRepository;
import com.boot.ksis.repository.upload.EncodedResourceRepository;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import com.boot.ksis.service.file.FileSizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EncodedResourceService {

    @Value("${uploadLocation}")
    String uploadLocation;

    @Value("${encodingLocation}")
    String encodingLocation;

    @Value("${filePath}")
    String dbFilePath;

    private final EncodedResourceRepository encodedResourceRepository;
    private final OriginalResourceRepository originalResourceRepository;
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationWebSocketHandler notificationWebSocketHandler;
    private final UploadLogRepository uploadLogRepository;
    private final FileSizeService fileSizeService;

    // 인코딩 정보를 데이터베이스에 저장하는 메서드
    public void saveEncodingInfo(Map<String, EncodingRequestDTO> encodings){
        createDirectoryIfNotExists(encodingLocation);

        // 각 파일에 대한 인코딩 정보
        encodings.forEach((fileName, request) -> {
            request.getEncodings().forEach(encoding -> {
                // 원본 리소스 조회
                Optional<OriginalResource> originalResourceOpt = originalResourceRepository.findByFileName(fileName);

                // 파일 이름에서 확장자 제거
                String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

                // 파일 이름 설정
                String outputFileName = baseName + "_" + encoding.get("resolution") + "." + encoding.get("format");

                // 제목 설정
                String encodedTitle = request.getTitle() + "_" + encoding.get("resolution") + "_" + encoding.get("format");

                // 경로 설정
                String path = dbFilePath + "/encoding/" + outputFileName;

                // 파일 유형 설정 (영상 확장자 목록)
                String[] videoExtensions = {"mp4", "avi", "mov", "mkv"};
                String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

                ResourceType resourceType;
                if (Arrays.asList(videoExtensions).contains(fileExtension)) {
                    resourceType = ResourceType.VIDEO;
                } else {
                    resourceType = ResourceType.IMAGE;
                }

                if(originalResourceOpt.isPresent()){
                    EncodedResource encodedResource = new EncodedResource();
                    encodedResource.setOriginalResource(originalResourceOpt.get());
                    encodedResource.setFileName(outputFileName);
                    encodedResource.setFileTitle(encodedTitle);
                    encodedResource.setFilePath(path);
                    encodedResource.setFormat(encoding.get("format"));
                    encodedResource.setResolution(encoding.get("resolution"));
                    encodedResource.setPlayTime(originalResourceOpt.get().getPlayTime());
                    encodedResource.setResourceStatus(ResourceStatus.UPLOADING);
                    encodedResource.setResourceType(resourceType);

                    // 데이터베이스 저장
                    encodedResourceRepository.save(encodedResource);
                }else{
                    throw new IllegalArgumentException("Original resource not found for fileName: " + fileName);
                }
            });
        });
    }

    // 각 파일들 인코딩 메서드
    public void startEncoding(Map<String, EncodingRequestDTO> encodings){
        // 각 파일에 대해 인코딩을 수행
        encodings.forEach((fileName, request) -> {
            File inputFile = new File(uploadLocation + fileName);
            request.getEncodings().forEach(encoding -> {
                String format = encoding.get("format");
                String resolution = encoding.get("resolution");
                String accountId = request.getAccountId(); // accountId 가져오기
                String resourceType = "";

                try {
                    // 파일의 MIME 타입을 확인
                    Path filePath = Paths.get(inputFile.getAbsolutePath());
                    String mimeType = Files.probeContentType(filePath);

                    // MIME 타입이 "video/"로 시작하는지 확인
                    if (mimeType != null && mimeType.startsWith("video/")) {
                        // 비디오 파일이면 비디오 인코딩 수행
                        encodeVideo(inputFile, format, resolution);

                        resourceType = "VIDEO";
                    } else {
                        // 그렇지 않으면 이미지 인코딩 수행
                        encodeImage(inputFile, format, resolution);

                        resourceType = "IMAGE";
                    }

                    // 인코딩 완료 후 상태 및 파일 용량 업데이트
                    updateEncodingStatus(fileName, format, resolution);

                    // 인코딩 알림 데이터베이스 저장
                    encodingNotification(accountId, fileName, format, resolution, resourceType);


                } catch (IOException e) {
                    failEncoding(accountId, fileName, format, resolution); // 인코딩 실패 데이터베이스 저장
                    e.printStackTrace();
                }
            });
        });
    }

    // 영상 인코딩 메서드
    private void encodeVideo(File inputFile, String format, String resolution) throws IOException {
        // 파일 이름에서 확장자 제거
        String baseName = inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));

        // 결과 파일 이름 생성
        String outputFileName = encodingLocation + baseName + "_" + resolution + "." + format;

        String command;

        switch (format.toLowerCase()) {
            case "mov":
                command = String.format("ffmpeg -i %s -vf scale=%s -c:v libx264 -c:a aac %s",
                        inputFile.getAbsolutePath(), getResolutionScale(resolution), outputFileName);
                break;
            case "avi":
                command = String.format("ffmpeg -i %s -vf scale=%s -c:v libxvid -c:a libmp3lame %s",
                        inputFile.getAbsolutePath(), getResolutionScale(resolution), outputFileName);
                break;
            case "mkv":
                command = String.format("ffmpeg -i %s -vf scale=%s -c:v libx264 -c:a aac %s",
                        inputFile.getAbsolutePath(), getResolutionScale(resolution), outputFileName);
                break;
            default:
                // 기본적으로 mp4로 인코딩
                command = String.format("ffmpeg -i %s -vf scale=%s -c:v libx264 -c:a aac %s",
                        inputFile.getAbsolutePath(), getResolutionScale(resolution), outputFileName);
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
    }

    // 이미지 인코딩 메서드
    private void encodeImage(File inputFile, String format, String resolution) throws IOException {
        // 파일 이름에서 확장자 제거
        String baseName = inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));
        // 결과 파일 이름 생성
        String outputFileName = encodingLocation + baseName + "_" + resolution + "." + format;
        String command;

        // 해상도에 따른 크기 설정
        Dimension newSize = getResolutionDimensions(resolution);
        String scaleFilter = String.format("scale=%d:%d", newSize.width, newSize.height);

        // 포맷에 따른 명령어 작성
        switch (format.toLowerCase()) {
            case "png", "bmp":
                command = String.format("ffmpeg -i %s -vf %s %s",
                        inputFile.getAbsolutePath(), scaleFilter, outputFileName);
                break;
            case "jpg":
                command = String.format("ffmpeg -i %s -vf %s -q:v 2 %s",  // -q:v 옵션은 JPG 품질 설정
                        inputFile.getAbsolutePath(), scaleFilter, outputFileName);
                break;
            default:
                throw new IOException("Unsupported image format: " + format);
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
    }

    // 영상 해상도 스케일 설정
    private String getResolutionScale(String resolution) {
        String[] parts = resolution.split("x");
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

    // 이미지 해상도 스케일 설정
    private Dimension getResolutionDimensions(String resolution) {
        String[] parts = resolution.split("x");
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

    // 리사이즈 메서드
    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedResizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = bufferedResizedImage.createGraphics();
        g2d.drawImage(resizedImage, 0, 0, null);
        g2d.dispose();

        return bufferedResizedImage;
    }

    // 인코딩 완료 후 상태 및 파일 용량 업데이트
    private void updateEncodingStatus(String fileName, String format, String resolution) {
        // 파일 이름에서 확장자 제거
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

        // 파일 이름 설정
        String outputFileName = baseName + "_" + resolution + "." + format;

        // 인코딩된 파일의 경로
        String filePath = encodingLocation + outputFileName;

        try {
            // 인코딩된 파일의 용량 가져오기
            long fileSize = getFileSize(filePath);

            // 데이터베이스에서 인코딩된 리소스 조회
            Optional<EncodedResource> encodedResourceOpt = encodedResourceRepository.findByFileName(outputFileName);

            if (encodedResourceOpt.isPresent()) {
                EncodedResource encodedResource = encodedResourceOpt.get();
                encodedResource.setFileSize((int) fileSize);
                encodedResource.setResourceStatus(ResourceStatus.COMPLETED);

                // 데이터베이스 업데이트
                encodedResourceRepository.save(encodedResource);

                //인코딩 용량 추가
                fileSizeService.updateTotalFileSize(encodedResource);

                // 업로드 로그 저장
                logSave(encodedResource, "인코딩 완료");

                // 알림 개수 업데이트
                notificationWebSocketHandler.sendToUser(encodedResource.getOriginalResource().getAccount().getAccountId(), "Notification Updated");
            } else {
                throw new IllegalArgumentException("Encoded resource not found for fileName: " + outputFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 인코딩 실패 데이터베이스 상태 FAIL 업데이트
    public void failEncoding(String accountId, String fileName, String format, String resolution){
        // 파일 이름에서 확장자 제거
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

        // 파일 이름 설정
        String outputFileName = baseName + "_" + resolution + "." + format;

        // 데이터베이스에서 인코딩된 리소스 조회
        Optional<EncodedResource> encodedResourceOpt = encodedResourceRepository.findByFileName(outputFileName);

        EncodedResource encodedResource = encodedResourceOpt.get();
        encodedResource.setResourceStatus(ResourceStatus.FAIL);
        encodedResourceRepository.save(encodedResource);

        failEncodingNotification(accountId, fileName, format, resolution); // 인코딩 실패 알림 데이터베이스 저장

        // 실패 로그
        logSave(encodedResource, "인코딩 실패");

    }

    // 경로에 있는 파일의 용량을 확인하는 메서드
    private long getFileSize(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            return Files.size(file.toPath());
        } else {
            throw new IOException("File not found: " + filePath);
        }
    }

    // 디렉토리 존재 여부를 확인하고 없으면 생성하는 메서드
    private void createDirectoryIfNotExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs(); // 디렉토리를 생성 (상위 디렉토리도 포함)
        }
    }

    // 인코딩 업로드 알림 데이터베이스 저장
    public void encodingNotification(String accountId, String fileName, String format, String resolution, String resourceType){

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // 파일 이름에서 확장자 제거
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

        // 파일 이름 설정
        String outputFileName = baseName + "_" + resolution + "." + format;

        // 데이터베이스에서 인코딩된 리소스 조회
        Optional<EncodedResource> encodedResourceOpt = encodedResourceRepository.findByFileName(outputFileName);

        EncodedResource encodedResource = encodedResourceOpt.get();

        String message = encodedResource.getFileTitle() + " 인코딩 성공";

        ResourceType Type = ResourceType.valueOf(resourceType);

        Notification notification = new Notification();
        notification.setIsRead(false);
        notification.setAccount(account);
        notification.setMessage(message);
        notification.setResourceType(Type);

        notificationRepository.save(notification);
    }

    // 인코딩 실패 알림 데이터베이스 저장
    public void failEncodingNotification(String accountId, String fileName, String format, String resolution){

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));

        // 파일 이름에서 확장자 제거
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

        // 파일 이름 설정
        String outputFileName = baseName + "_" + resolution + "." + format;

        // 데이터베이스에서 인코딩된 리소스 조회
        Optional<EncodedResource> encodedResourceOpt = encodedResourceRepository.findByFileName(outputFileName);

        EncodedResource encodedResource = encodedResourceOpt.get();

        String message = encodedResource.getFileTitle() + " 인코딩 실패";

        Notification notification = new Notification();
        notification.setIsRead(false);
        notification.setAccount(account);
        notification.setMessage(message);
        notification.setResourceType(ResourceType.IMAGE);

        notificationRepository.save(notification);

        // 알림 개수 업데이트
        notificationWebSocketHandler.sendToUser(account.getAccountId(), "Notification Updated");

    }

    // 업로드 로그 저장
    private void logSave(EncodedResource encodedResource, String message){
        String accountId = encodedResource.getOriginalResource().getAccount().getAccountId();
        Account account = accountRepository.findByAccountId(accountId).orElseThrow(null);

        UploadLog uploadLog = new UploadLog();
        uploadLog.setMessage(encodedResource.getFileTitle() + " " + message);
        uploadLog.setAccount(account);
        uploadLogRepository.save(uploadLog);
    }



}


