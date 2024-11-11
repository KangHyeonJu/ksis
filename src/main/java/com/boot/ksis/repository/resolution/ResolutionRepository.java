package com.boot.ksis.repository.resolution;

import com.boot.ksis.entity.Resolution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResolutionRepository extends JpaRepository<Resolution, Long> {
    Resolution findByResolutionId(Long resolutionId);

    @Query("SELECT r FROM Resolution r WHERE CAST(r.height AS string) LIKE %:searchTerm%")
    Page<Resolution> searchByHeight(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT r FROM Resolution r WHERE CAST(r.width AS string) LIKE %:searchTerm%")
    Page<Resolution> searchByWidth(@Param("searchTerm") String searchTerm, Pageable pageable);


    Page<Resolution> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Resolution> findByWidth(int width);

    Resolution findByHeight(int height);
}
