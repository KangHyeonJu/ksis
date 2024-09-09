package com.boot.ksis.repository.file;

import com.boot.ksis.entity.FileSize;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileSizeRepository extends JpaRepository<FileSize, Integer> {
    FileSize findByFileSizeId(int fileSizeId);
}
