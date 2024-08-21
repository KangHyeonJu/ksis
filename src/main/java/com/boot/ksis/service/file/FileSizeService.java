package com.boot.ksis.service.file;

import com.boot.ksis.entity.FileSize;
import com.boot.ksis.repository.file.FileSizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileSizeService {

    @Autowired
    private FileSizeRepository fileSizeRepository;

    // Get the current file size settings
    public FileSize getFileSize() {
        return fileSizeRepository.findById(1).orElseGet(() -> {
            // If not found, create a new one with default values
            FileSize defaultFileSize = new FileSize();
            defaultFileSize.setImageMaxSize(10);
            defaultFileSize.setVideoMaxSize(50);
            return fileSizeRepository.save(defaultFileSize);
        });
    }

    // Update the file size settings
    public FileSize updateFileSize(FileSize fileSize) {
        return fileSizeRepository.save(fileSize);
    }
}
