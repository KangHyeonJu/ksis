package com.boot.ksis.service.file;

import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.file.FileSizeDTO;
import com.boot.ksis.dto.file.TotalFileCountDTO;
import com.boot.ksis.dto.file.TotalFileSizeDTO;
import com.boot.ksis.entity.EncodedResource;
import com.boot.ksis.entity.FileSize;
import com.boot.ksis.repository.file.FileSizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileSizeService {

    private final FileSizeRepository fileSizeRepository;

    // 현재 전체 파일 크기 설정 조회
    public FileSizeDTO getFileSize() {
        FileSize fileSize = createFileSize();
        return convertToDTO(fileSize);
    }


    // 파일 크기 설정 업데이트
    public FileSizeDTO updateFileSize(FileSizeDTO fileSizeDTO) {
        // 기존 설정을 조회
        FileSize existingFileSize = fileSizeRepository.findById(1).orElseThrow(() ->
                new IllegalStateException("File size settings not found.")
        );

        // 기존 설정 값을 새로운 값으로 업데이트
        existingFileSize.setImageMaxSize(fileSizeDTO.getImageMaxSize());
        existingFileSize.setVideoMaxSize(fileSizeDTO.getVideoMaxSize());

        // 업데이트된 설정 저장
        FileSize updatedFileSize = fileSizeRepository.save(existingFileSize);

        return convertToDTO(updatedFileSize);
    }

    // 엔티티를 DTO로 변환
    private FileSizeDTO convertToDTO(FileSize fileSize) {
        return new FileSizeDTO(
                fileSize.getImageMaxSize(),
                fileSize.getVideoMaxSize()
        );
    }

    // DTO를 엔티티로 변환
    private FileSize convertToEntity(FileSizeDTO fileSizeDTO) {
        FileSize fileSize = new FileSize();
        fileSize.setImageMaxSize(fileSizeDTO.getImageMaxSize());
        fileSize.setVideoMaxSize(fileSizeDTO.getVideoMaxSize());
        return fileSize;
    }

    //파일 총 용량
    public TotalFileSizeDTO getTotalFileSize(){
        FileSize fileSize = createFileSize();

        return TotalFileSizeDTO.builder()
                .totalImageSize(fileSize.getTotalImage())
                .totalVideoSize(fileSize.getTotalVideo())
                .totalEncodedImageSize(fileSize.getTotalEncodedImage())
                .totalEncodedVideoSize(fileSize.getTotalEncodedVideo())
                .build();
    }

    public void updateTotalFileSize(EncodedResource encodedResource){
        //인코딩 용량 추가
        FileSize fileSize = createFileSize();
        if(encodedResource.getResourceType() == ResourceType.IMAGE){
            fileSize.setTotalEncodedImage(fileSize.getTotalEncodedImage() + encodedResource.getFileSize());
            fileSize.setCountEncodedImage(fileSize.getCountEncodedImage() + 1);
        }else {
            fileSize.setTotalEncodedVideo(fileSize.getTotalEncodedVideo() + encodedResource.getFileSize());
            fileSize.setCountEncodedVideo(fileSize.getCountEncodedVideo() + 1);
        }
        fileSizeRepository.save(fileSize);
    }

    public TotalFileCountDTO getTotalFileCount(){
        FileSize fileSize = createFileSize();

        return TotalFileCountDTO.builder()
                .countImage(fileSize.getCountImage())
                .countVideo(fileSize.getCountVideo())
                .countEncodedImage(fileSize.getCountEncodedImage())
                .countEncodedVideo(fileSize.getCountEncodedVideo())
                .build();
    }

    public FileSize createFileSize(){
        return fileSizeRepository.findById(1).orElseGet(() -> {
            // 설정이 없으면 기본값으로 새로운 설정 생성
            FileSize defaultFileSize = new FileSize();
            defaultFileSize.setImageMaxSize(10);
            defaultFileSize.setVideoMaxSize(500);
            return fileSizeRepository.save(defaultFileSize);
        });
    }
}
