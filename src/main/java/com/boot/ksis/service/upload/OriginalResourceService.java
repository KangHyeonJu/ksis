package com.boot.ksis.service.upload;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.dto.upload.OriginalResourceDTO;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.entity.ThumbNail;
import com.boot.ksis.repository.signage.ThumbNailRepository;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OriginalResourceService {

    private final OriginalResourceRepository originalResourceRepository;
    private final ThumbNailRepository thumbNailRepository;

    @Value("${uploadLocation}")
    String uploadLocation;

    @Value("${thumbnailsLocation}")
    String thumbnailsLocation;

    // 파일 메타데이터 저장
    public List<OriginalResourceDTO> saveToDatabase(
            List<OriginalResourceDTO> originalResourceDTOS,
            List<MultipartFile> files
    ) throws IOException {
        // 디렉토리 생성 확인
        createDirectoryIfNotExists(uploadLocation);
        createDirectoryIfNotExists(thumbnailsLocation);

        List<OriginalResourceDTO> returnDTO = new ArrayList<>();

        for(int i = 0; i < files.size(); i++){
            MultipartFile file = files.get(i);
            OriginalResourceDTO dto = originalResourceDTOS.get(i);
            
            // DTO 넣을 값들 설정
            String uuidFileName = UUID.randomUUID().toString() + "." + dto.getFormat(); // 파일 이름 생성
            String filePath = "/file/uploads/" + uuidFileName; // 파일 경로 설정
            long fileSize = file.getSize(); // 파일 용량 설정

            // DTO 업데이트
            dto.setFilename(uuidFileName);
            dto.setFilePath(filePath);
            dto.setFileSize(fileSize);

            // DTO 값들을 엔티티에 넣어준다
            OriginalResource originalResource = dto.createNewSignage();
            
            // 엔티티를 데이터베이스에 저장
            originalResourceRepository.save(originalResource);

            // DTO들을 리스트에 저장
            returnDTO.add(dto);
        }

        // 저장된 DTO 리스트 반환
        return returnDTO;
    }

    // 청크 업로드
    public ResponseEntity<String> chunkUpload(MultipartFile chunk, String fileName, int chunkIndex, int totalChunks){
        int CHUNK_SIZE = 1 * 1024 * 1024;
        try{
            // UUID로 저장된 파일 이름을 사용
            String filePath = uploadLocation + fileName;
            String fileNameUUID = UUID.randomUUID().toString() + ".jpg";

            String thumbnailPath = thumbnailsLocation + fileNameUUID;
            String thumbnailUrl = "/file/thumbnails/" + fileNameUUID;

            File file = new File(filePath);

            // 청크를 이어붙이기 위한 파일 채널 열기
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(chunkIndex * CHUNK_SIZE); // 파일의 위치를 청크 시작점으로 이동
            raf.write(chunk.getBytes()); // 청크 데이터를 파일에 기록
            raf.close();

            OriginalResource originalResource = updateStatus(fileName);

            // 파일 저장 확인 후 썸네일 생성 및 저장 호출
            if (chunkIndex + 1 == totalChunks) {
                String fileExtension = getFileExtension(fileName).toLowerCase();
                if (fileExtension.equals(".mp4") || fileExtension.equals(".avi") || fileExtension.equals(".mkv")) {
                    // 동영상인 경우
                    generateVideoThumbnail(filePath,thumbnailPath, thumbnailUrl, originalResource);
                } else if (fileExtension.equals(".png") || fileExtension.equals(".jpg") || fileExtension.equals(".jpeg")) {
                    // 이미지인 경우
                    generateImageThumbnail(filePath,thumbnailPath, thumbnailUrl, originalResource);
                } else {
                    System.out.println("지원하지 않는 파일 형식: " + fileExtension);
                }
            }

            return ResponseEntity.ok("청크 업로드 성공");
        }catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("청크 업로드 실패: " + e.getMessage());
        }

    }

    // 상태를 COMPLETED로 업데이트
    public OriginalResource updateStatus(String uuidFileName){

        OriginalResource originalResource = originalResourceRepository.findByFileName(uuidFileName)
                .orElseThrow(() -> new RuntimeException("File not found: " + uuidFileName));

        // 상태를 COMPLETED로 변경
        originalResource.setResourceStatus(ResourceStatus.COMPLETED);

        // 변경된 상태를 데이터베이스에 저장
        originalResourceRepository.save(originalResource);

        return originalResource;
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

    // 디렉토리 존재 여부를 확인하고 없으면 생성하는 메서드
    private void createDirectoryIfNotExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs(); // 디렉토리를 생성 (상위 디렉토리도 포함)
        }
    }

    // 이미지 썸네일 생성 메서드
    private void generateImageThumbnail(String imagePath,String thumbnailPath, String thumbnailUrl, OriginalResource originalResource) throws IOException {

        // 이미지 파일을 읽어와서 썸네일을 생성하고 저장
        Thumbnails.of(new File(imagePath))
                .size(200, 200) // 원하는 썸네일 크기 설정
                .outputFormat("jpg")
                .toFile(new File(thumbnailPath));

        // 썸네일 메타데이터를 데이터베이스에 저장
        ThumbNail thumbnail = new ThumbNail();
        thumbnail.setOriginalResource(originalResource);
        thumbnail.setFilePath(thumbnailUrl);
        thumbnail.setFileSize((int) (new File(thumbnailPath).length() / 1024)); // 용량(KB 단위)

        thumbNailRepository.save(thumbnail); // 썸네일 저장
    }

    // 동영상 썸네일 생성 메서드
    private void generateVideoThumbnail(String videoPath,String thumbnailPath, String thumbnailUrl, OriginalResource originalResource) throws IOException{
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
            thumbnail.setFilePath(thumbnailUrl);
            thumbnail.setFileSize((int) (new File(thumbnailPath).length() / 1024)); // 용량(KB 단위)

            thumbNailRepository.save(thumbnail); // 썸네일 저장
        }finally {
            frameGrabber.stop();
        }
    }

}