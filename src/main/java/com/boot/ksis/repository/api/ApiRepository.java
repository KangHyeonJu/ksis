package com.boot.ksis.repository.api;

import com.boot.ksis.entity.API;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiRepository extends JpaRepository<API, Long> {
Page<API> findByApiNameContainingIgnoreCase(String apiName, Pageable pageable);
Page<API> findByProviderContainingIgnoreCase(String provider, Pageable pageable);
Page<API> findByExpiryDateContainingIgnoreCase(String expiryDate, Pageable pageable);
}
