package com.boot.ksis.service.upload;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.upload.OriginalResourceDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.FileSize;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.entity.ThumbNail;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.file.FileSizeRepository;
import com.boot.ksis.repository.signage.ThumbNailRepository;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OriginalResourceService {

    // 파일별로 청크 업로드 작업을 처리하는 스레드 풀을 저장
    private final Map<String, ExecutorService> uploadExecutors = new ConcurrentHashMap<>();

    // 스레드 풀을 위한 설정
    private final int THREAD_POOL_SIZE = 1;

    private final OriginalResourceRepository originalResourceRepository;
    private final ThumbNailRepository thumbNailRepository;
    private final FileSizeRepository fileSizeRepository;
    private final AccountRepository accountRepository;

    @Value("${uploadLocation}")
    String uploadLocation;

    @Value("${thumbnailsLocation}")
    String thumbnailsLocation;

    @Value("${filePath}")
    String dbFilePath;


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

            // accountId를 통해 Account 엔티티를 데이터베이스에서 조회
            Account account = accountRepository.findById(dto.getAccount())
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            
            // DTO 넣을 값들 설정
            String uuidFileName = UUID.randomUUID().toString() + "." + dto.getFormat(); // 파일 이름 생성
            String filePath = dbFilePath + "/uploads/" + uuidFileName; // 파일 경로 설정
            long fileSize = file.getSize(); // 파일 용량 설정

            // DTO 업데이트
            dto.setFilename(uuidFileName);
            dto.setFilePath(filePath);
            dto.setFileSize(fileSize);

            // DTO 값들을 엔티티에 넣어준다
            OriginalResource originalResource = dto.createNewSignage();
            originalResource.setAccount(account); // 조회된 account 설정
            
            // 엔티티를 데이터베이스에 저장
            originalResourceRepository.save(originalResource);

            // DTO들을 리스트에 저장
            returnDTO.add(dto);
        }

        // 저장된 DTO 리스트 반환
        return returnDTO;
    }

    // 청크 업로드
    @Async
    public ResponseEntity<String> chunkUpload(MultipartFile chunk, String fileName, int chunkIndex, int totalChunks){
        int CHUNK_SIZE = 1 * 1024 * 1024;
        // UUID로 저장된 파일 이름을 사용
        String filePath = uploadLocation + fileName;

        // 파일 이름에 고유한 스레드를 할당하여 순차적으로 청크를 처리
        ExecutorService executorService = uploadExecutors.computeIfAbsent(fileName, key -> createExecutor());

        CompletableFuture.runAsync(() -> {
            try {
                File file = new File(filePath);

                // 청크를 이어붙이기 위한 파일 채널 열기
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.seek(chunkIndex * CHUNK_SIZE); // 파일의 위치를 청크 시작점으로 이동
                raf.write(chunk.getBytes()); // 청크 데이터를 파일에 기록
                raf.close();

                // 마지막 청크라면 처리 완료 후 썸네일 생성
                if (chunkIndex + 1 == totalChunks) {
                    OriginalResource originalResource = updateStatus(fileName);

                    String fileExtension = getFileExtension(fileName).toLowerCase();
                    String fileNameUUID = UUID.randomUUID().toString() + ".jpg";
                    String thumbnailPath = thumbnailsLocation + fileNameUUID;
                    String thumbnailUrl = dbFilePath + "/thumbnails/" + fileNameUUID;

                    if (fileExtension.equals(".mp4") || fileExtension.equals(".avi") || fileExtension.equals(".mkv")) {
                        generateVideoThumbnail(filePath, thumbnailPath, thumbnailUrl, originalResource);
                    } else if (fileExtension.equals(".png") || fileExtension.equals(".jpg") || fileExtension.equals(".jpeg")) {
                        generateImageThumbnail(filePath, thumbnailPath, thumbnailUrl, originalResource);
                    } else {
                        System.out.println("지원하지 않는 파일 형식: " + fileExtension);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }, executorService).join(); // join()을 사용하여 업로드가 완료될 때까지 기다림

        return ResponseEntity.ok("청크 업로드 성공");
    }

    // 상태를 COMPLETED로 업데이트
    public OriginalResource updateStatus(String uuidFileName){

        OriginalResource originalResource = originalResourceRepository.findByFileName(uuidFileName)
                .orElseThrow(() -> new RuntimeException("File not found: " + uuidFileName));

        // 상태를 COMPLETED로 변경
        originalResource.setResourceStatus(ResourceStatus.COMPLETED);

        // 변경된 상태를 데이터베이스에 저장
        originalResourceRepository.save(originalResource);

        //원본 용량 추가
        FileSize fileSize = fileSizeRepository.findById(1).orElseGet(() -> {
            // 설정이 없으면 기본값으로 새로운 설정 생성
            FileSize defaultFileSize = new FileSize();
            defaultFileSize.setTotalVideo(0L);
            defaultFileSize.setTotalImage(0L);
            return fileSizeRepository.save(defaultFileSize);
        });

        if(originalResource.getResourceType() == ResourceType.IMAGE){
            fileSize.setTotalImage(fileSize.getTotalImage() + originalResource.getFileSize());
            fileSize.setCountImage(fileSize.getCountImage() + 1);
        }else {
            fileSize.setTotalVideo(fileSize.getTotalVideo() + originalResource.getFileSize());
            fileSize.setCountVideo(fileSize.getCountVideo() + 1);
        }

        fileSizeRepository.save(fileSize);

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

    // 동영상에서 썸네일 생성
    private void generateVideoThumbnail(String videoPath, String thumbnailPath, String thumbnailUrl, OriginalResource originalResource) throws IOException {
        // FFmpeg 명령어를 사용하여 동영상에서 프레임 추출
        extractFrame(videoPath, thumbnailPath);

        // 추출한 이미지를 썸네일 크기로 조정하고 파일로 저장
        BufferedImage bufferedImage = ImageIO.read(new File(thumbnailPath));
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
    }

    // FFmpeg를 사용하여 특정 프레임 추출
    private void extractFrame(String videoPath, String outputPath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", "-i", videoPath, "-ss", "00:00:01", "-vframes", "1", outputPath
        );
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // FFmpeg 명령어 실행 결과 로그 읽기
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // 디버깅용 로그
            }
        }

        // 프로세스 종료 대기
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg process failed with exit code " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("FFmpeg process was interrupted", e);
        }
    }

    // 업로드 중 파일 삭제 메서드
    public void deleteFile(Map<String, String> request){
        String fileTitle = request.get("fileName");
        String accountId = request.get("accountId");

        Optional<OriginalResource> resource = originalResourceRepository.findByFileTitle(fileTitle);

        try{
            Path fileToDeletePath = Paths.get(uploadLocation + resource.get().getFileName());
            Files.delete(fileToDeletePath);
            OriginalResource originalResource = originalResourceRepository.findByOriginalResourceId(resource.get().getOriginalResourceId());
            originalResourceRepository.delete(originalResource);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // 제목 중복 검증 메서드
    public List<String> titleVerification(List<String> titles){
        List<String> sameTitles = new ArrayList<>(); // 중복된 제목을 저장할 리스트

        for(String title : titles){
            Optional<OriginalResource> existingResource = originalResourceRepository.findByFileTitle(title);
            if (existingResource.isPresent()) {
                sameTitles.add(title); // 중복된 제목을 리스트에 추가
            }
        }

        return sameTitles;
    }

    // 스레드 풀 생성
    private ExecutorService createExecutor() {
        return Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    // 파일이 완료되면 스레드 풀 제거
    private void cleanupExecutor(String fileName) {
        ExecutorService executorService = uploadExecutors.remove(fileName);
        if (executorService != null) {
            executorService.shutdown();
        }
    }

}
