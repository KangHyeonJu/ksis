package com.boot.ksis.service.upload;

import com.boot.ksis.repository.upload.EncodedResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EncodedResourceService {

    private final EncodedResourceRepository encodedResourceRepository;

    private final String ENCODING_DIR = "C:\\Users\\codepc\\git\\ksis\\src\\main\\resources\\encoding\\";

    private final String UPLOAD_DIR = "C:\\Users\\codepc\\git\\ksis\\src\\main\\resources\\uploads\\";

    // 각 파일들 인코딩 메서드
    public void startEncoding(Map<String, List<Map<String, String>>> encodings){
        // 각 파일에 대해 인코딩을 수행
        encodings.forEach((fileName, encodingList) -> {
            File inputFile = new File(UPLOAD_DIR + fileName);
            encodingList.forEach(encoding -> {
                String format = encoding.get("format");
                String resolution = encoding.get("resolution");

                // 인코딩 작업 수행
                try{
                    if(fileName.endsWith(".mp4")){
                        encodeVideo(inputFile, format, resolution);
                    }else{
                        encodeImage(inputFile, format, resolution);
                    }
                }catch(IOException e){
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
        String outputFileName = ENCODING_DIR + baseName + "_" + resolution + "." + format;

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
                command = String.format("ffmpeg -i %s -vf scale=%s -c:v copy -c:a copy %s",
                        inputFile.getAbsolutePath(), getResolutionScale(resolution), outputFileName);
                break;
            default:
                // 기본적으로 mp4로 인코딩
                command = String.format("ffmpeg -i %s -vf scale=%s -c:v libx264 -c:a aac %s",
                        inputFile.getAbsolutePath(), getResolutionScale(resolution), outputFileName);
                break;
        }

        Process process = Runtime.getRuntime().exec(command);
        try{
            process.waitFor(); // 프로세스 완료 대기
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
        System.out.println("Video encoded: " + outputFileName);
    }

    // 이미지 인코딩 메서드
    private void encodeImage(File inputFile, String format, String resolution) throws IOException {
        // 파일 이름에서 확장자 제거
        String baseName = inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));
        // 결과 파일 이름 생성
        String outputFileName = ENCODING_DIR + baseName + "_" + resolution + "." + format;
        String command;

        // 해상도에 따른 크기 설정
        Dimension newSize = getResolutionDimensions(resolution);
        String scaleFilter = String.format("scale=%d:%d", newSize.width, newSize.height);

        // 포맷에 따른 명령어 작성
        switch (format.toLowerCase()) {
            case "png":
                command = String.format("ffmpeg -i %s -vf %s %s",
                        inputFile.getAbsolutePath(), scaleFilter, outputFileName);
                break;
            case "jpg":
                command = String.format("ffmpeg -i %s -vf %s -q:v 2 %s",  // -q:v 옵션은 JPG 품질 설정
                        inputFile.getAbsolutePath(), scaleFilter, outputFileName);
                break;
            case "bmp":
                command = String.format("ffmpeg -i %s -vf %s %s",
                        inputFile.getAbsolutePath(), scaleFilter, outputFileName);
                break;
            default:
                throw new IOException("Unsupported image format: " + format);
        }

        Process process = Runtime.getRuntime().exec(command);
        try {
            process.waitFor(); // 프로세스 완료 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Image encoded: " + outputFileName);
    }

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

    // 리사이즈 메서드
    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedResizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = bufferedResizedImage.createGraphics();
        g2d.drawImage(resizedImage, 0, 0, null);
        g2d.dispose();

        return bufferedResizedImage;
    }

}
