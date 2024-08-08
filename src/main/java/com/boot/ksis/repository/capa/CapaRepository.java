package com.boot.ksis.repository.capa;

import com.boot.ksis.entity.FileSize;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CapaRepository extends JpaRepository<FileSize, Integer> {
}
