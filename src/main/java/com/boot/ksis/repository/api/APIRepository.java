package com.boot.ksis.repository.api;

import com.boot.ksis.entity.API;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface APIRepository extends JpaRepository<API, Long> {
}
