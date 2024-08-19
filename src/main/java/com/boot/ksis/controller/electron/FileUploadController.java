package com.boot.ksis.controller.electron;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.OriginalResourceDTO;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.entity.ThumbNail;
import com.boot.ksis.service.upload.OriginalResourceService;
import com.boot.ksis.service.upload.ThumbnailService;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FileUploadController {

    private final OriginalResourceService originalResourceService;
    private final ThumbnailService thumbnailService;

    // 파일이 저장되는 경로
    private final String UPLOAD_DIR = "C:\\Users\\codepc\\git\\ksis\\src\\main\\resources\\uploads\\";

    // 썸네일이 저장되는 경로
    private final String THUMBNAIL_DIR = "C:\\Users\\codepc\\git\\ksis\\src\\main\\resources\\thumbnails\\";

    @PostMapping("/filedatasave")
    public ResponseEntity<HashMap<String, String>> uploadFile(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("titles") String[] titles,
            @RequestParam("formats") String[] formats,
            @RequestParam("resolutions") String[] resolutions,
            @RequestParam("playTimes") String[] playTimes,
            @RequestParam("statuses") String[] statuses,
            @RequestParam("resourceTypes") String[] resourceTypes
    ){
        HashMap<String, String> fileNamesMap = new HashMap<>();
        try {
            for (int i = 0; i < files.length; i++) {
                // UUID를 생성하여 파일 이름으로 사용
                String originalFileName = files[i].getOriginalFilename();
                String uuidFileName = UUID.randomUUID().toString() + getFileExtension(originalFileName);
                String filePath = UPLOAD_DIR + uuidFileName;
                ResourceStatus resourceStatusEnum = ResourceStatus.valueOf(statuses[i].toUpperCase());
                ResourceType resourceTypeEnum = ResourceType.valueOf(resourceTypes[i].toUpperCase());

                OriginalResourceDTO originalResourceDTO = new OriginalResourceDTO(
                        uuidFileName,
                        titles[i],
                        filePath,
                        Float.parseFloat(playTimes[i]),
                        formats[i],
                        resolutions[i],
                        files[i].getSize(),
                        resourceStatusEnum,
                        resourceTypeEnum
                );

                originalResourceService.saveToDatabase(originalResourceDTO);

                // 파일 이름을 맵에 추가
                fileNamesMap.put("file" + i, uuidFileName);
            }

            return ResponseEntity.ok(fileNamesMap);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new HashMap<String, String>() {{
                        put("error", "Invalid status or resource type: " + e.getMessage());
                    }});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("error", "파일 데이터베이스 저장 실패: " + e.getMessage());
                    }});
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("fileNames") String[] fileNames
    ) {
        try {
            for (int i = 0; i < files.length; i++) {
                // UUID로 저장된 파일 이름을 사용
                String uuidFileName = fileNames[i];
                String filePath = UPLOAD_DIR + uuidFileName;
                String thumbnailPath = THUMBNAIL_DIR + UUID.randomUUID().toString() + ".jpg";

                // 파일을 해당 경로에 저장
                files[i].transferTo(new java.io.File(filePath));

                // 상태를 COMPLETED로 업데이트
                OriginalResource originalResource = originalResourceService.updateStatus(uuidFileName);

                // 썸네일 생성 및 저장 호출
                String fileExtension = getFileExtension(uuidFileName).toLowerCase();
                if (fileExtension.equals(".mp4") || fileExtension.equals(".avi") || fileExtension.equals(".mkv")) {
                    // 동영상인 경우
                    generateVideoThumbnail(filePath, thumbnailPath, originalResource);
                } else {
                    // 이미지인 경우
                    generateImageThumbnail(filePath, thumbnailPath, originalResource);
                }
            }

            return ResponseEntity.ok("파일 업로드 완료");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 실패: " + e.getMessage());
        }
    }

    // 이미지 썸네일 생성 메서드
    private void generateImageThumbnail(String imagePath, String thumbnailPath, OriginalResource originalResource) throws IOException {
        // 이미지 파일을 읽어와서 썸네일을 생성하고 저장
        Thumbnails.of(new File(imagePath))
                .size(200, 200) // 원하는 썸네일 크기 설정
                .outputFormat("jpg")
                .toFile(new File(thumbnailPath));

        // 썸네일 메타데이터를 데이터베이스에 저장
        ThumbNail thumbnail = new ThumbNail();
        thumbnail.setOriginalResource(originalResource);
        thumbnail.setFilePath(thumbnailPath);
        thumbnail.setFileSize((int) (new File(thumbnailPath).length() / 1024)); // 용량(KB 단위)

        thumbnailService.saveThumbnail(thumbnail); // 썸네일 저장 서비스 호출
    }

    // 동영상 썸네일 생성 메서드
    private void generateVideoThumbnail(String videoPath, String thumbnailPath, OriginalResource originalResource) throws IOException{
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoPath);
        try{
            frameGrabber.start();

            // 비디오의 첫 번째 프레임을 가져옴
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage bufferedImage = converter.convert(frameGrabber.grab());

            // 이미지를 썸네일 크기로 조정하고 파일로 저장
            BufferedImage thumbnailImage = Thumbnails.of(bufferedImage)
                    .size(200, 200)
                    .asBufferedImage();

            ImageIO.write(thumbnailImage, "jpg", new File(thumbnailPath));

            // 썸네일 메타데이터를 데이터베이스에 저장
            ThumbNail thumbnail = new ThumbNail();
            thumbnail.setOriginalResource(originalResource);
            thumbnail.setFilePath(thumbnailPath);
            thumbnail.setFileSize((int) (new File(thumbnailPath).length() / 1024)); // 용량(KB 단위)

            thumbnailService.saveThumbnail(thumbnail); // 썸네일 저장 서비스 호출
        }finally {
            frameGrabber.stop();
        }
    }

    // 확장자 얻는 메서드
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        } else {
            return "";
        }
    }

}
