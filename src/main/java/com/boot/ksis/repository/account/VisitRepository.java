package com.boot.ksis.repository.account;

import com.boot.ksis.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query("SELECT v.visitDate as visitDate, COUNT(v) as visitCount " + "FROM Visit v " + "WHERE v.visitDate BETWEEN :startDate AND :endDate " + "GROUP BY v.visitDate " + "ORDER BY v.visitDate ASC")
    List<Object[]> countVisitsByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
