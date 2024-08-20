package com.boot.ksis.repository.upload;

import com.boot.ksis.entity.ThumbNail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThumbNailRepository extends JpaRepository<ThumbNail, Long> {
}
