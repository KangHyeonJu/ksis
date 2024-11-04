package com.boot.ksis.repository.notice;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.constant.Role;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {


    Optional<Notice> findByNoticeIdOrderByRegTimeDesc(Long noticeId);

    @Query("SELECT no FROM Notice no WHERE (no.account.accountId LIKE %:searchTerm% OR no.account.name LIKE %:searchTerm%)")
    List<Notice> searchByAccountIdOrName(@Param("searchTerm") String searchTerm);

    // 관리자 조회
    Page<Notice> findByIsActiveAndTitleContainingIgnoreCase(boolean isActive, String noticeTitle, Pageable pageable);

    @Query("SELECT no FROM Notice no WHERE (no.account.accountId LIKE %:searchTerm% OR no.account.name LIKE %:searchTerm%) AND no.isActive = :isActive")
    Page<Notice> searchByAccountOrNameAndIsActive(@Param("searchTerm") String searchTerm, @Param("isActive") boolean isActive, Pageable pageable);

    @Query("SELECT no FROM Notice no WHERE CAST(no.regTime AS string) LIKE %:searchTerm% AND no.isActive = :isActive")
    Page<Notice> searchByRegTimeContainingIgnoreCaseAndIsActive(@Param("searchTerm") String searchTerm, @Param("isActive") boolean isActive, Pageable pageable);

    Page<Notice> findByIsActive(boolean isActive, Pageable pageable);

    // 유저 조회
    Page<Notice> findByIsActiveAndAccountAndTitleContainingIgnoreCase(boolean isActive, Account accountId, String noticeTitle, Pageable pageable);

    Page<Notice> findByAccount_RoleAndIsActiveAndTitleContainingIgnoreCase(Role role, boolean isActive, String noticeTitle, Pageable pageable);

    @Query("SELECT no FROM Notice no WHERE (no.account.accountId LIKE %:searchTerm% OR no.account.name LIKE %:searchTerm%) AND no.isActive = :isActive AND no.account.accountId = :accountId")
    Page<Notice> searchByAccountOrNameAndIsActiveAndAccount(@Param("searchTerm") String searchTerm, @Param("isActive") boolean isActive, @Param("accountId") Account accountId, Pageable pageable);

    @Query("SELECT no FROM Notice no WHERE (no.account.accountId LIKE %:searchTerm% OR no.account.name LIKE %:searchTerm%) AND no.account.role = :role AND no.isActive = :isActive")
    Page<Notice> searchByAccountOrNameAndAccount_RoleAndIsActive(@Param("searchTerm") String searchTerm, @Param("role") Role role, @Param("isActive") boolean isActive, Pageable pageable);

    @Query("SELECT no FROM Notice no WHERE CAST(no.regTime AS string) LIKE %:searchTerm% AND no.isActive = :isActive AND no.account = :accountId")
    Page<Notice> searchByRegTimeContainingIgnoreCaseAndIsActiveAndAccount(@Param("searchTerm") String searchTerm, @Param("isActive") boolean isActive, @Param("accountId") Account accountId, Pageable pageable);

    @Query("SELECT no FROM Notice no WHERE CAST(no.regTime AS string) LIKE %:searchTerm% AND no.account.role = :role AND no.isActive = :isActive")
    Page<Notice> searchByRegTimeContainingIgnoreCaseAndAccount_RoleAndIsActive(@Param("searchTerm") String searchTerm, @Param("role") Role role, @Param("isActive") boolean isActive, Pageable pageable);

    Page<Notice> findByIsActiveAndAccount(boolean isActive, Account accountId, Pageable pageable);
    Page<Notice> findByAccount_RoleAndIsActive(Role role, boolean isActive, Pageable pageable);


    //활성화 공지
    //관리자 공지 활성화된거
    @Query("SELECT n FROM Notice n " +
            "JOIN n.account a " +
            "WHERE n.isActive = true " +
            "ORDER BY a.role ASC, n.regTime DESC")
    Page<Notice> findActiveNoticesWithAccountsOrdered(Pageable pageable);


    //비활성화 공지
    //관리자 공지 비활성화 된거
    @Query("SELECT n FROM Notice n " +
            "JOIN n.account a " +
            "WHERE n.isActive = false " +
            "ORDER BY n.regTime DESC")
    Page<Notice> findDeActivationNoticesWithAccountsOrdered(Pageable pageable);


}