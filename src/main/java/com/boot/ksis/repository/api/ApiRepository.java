package com.boot.ksis.repository.api;

import com.boot.ksis.entity.API;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ApiRepository extends JpaRepository<API, Long> {
Page<API> findByApiNameContainingIgnoreCase(String apiName, Pageable pageable);
Page<API> findByProviderContainingIgnoreCase(String provider, Pageable pageable);
Page<API> findByExpiryDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
