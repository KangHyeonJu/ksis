package com.boot.ksis.repository.signage;

import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.entity.ThumbNail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbNailRepository extends JpaRepository<ThumbNail, Long> {
    ThumbNail findByOriginalResource(OriginalResource originalResource);
    void deleteByOriginalResource(OriginalResource originalResource);
}
