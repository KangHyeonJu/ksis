package com.boot.ksis.repository.upload;

import com.boot.ksis.entity.OriginalResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OriginalResourceRepository extends JpaRepository<OriginalResource, Long> {
}
