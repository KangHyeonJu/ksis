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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {


    Optional<Notice> findByNoticeIdOrderByRegTimeDesc(Long noticeId);

    @Query("SELECT no FROM Notice no WHERE (no.account.accountId LIKE %:searchTerm% OR no.account.name LIKE %:searchTerm%)")
    List<Notice> searchByAccountIdOrName(@Param("searchTerm") String searchTerm);



    // 유저 조회
    Page<Notice> findByIsActiveAndAccountAndTitleContainingIgnoreCase(boolean isActive, Account accountId, String noticeTitle, Pageable pageable);

    @Query("SELECT no FROM Notice no WHERE CAST(no.regTime AS string) LIKE %:searchTerm% AND no.isActive = :isActive AND no.account = :accountId")
    Page<Notice> searchByRegTimeContainingIgnoreCaseAndIsActiveAndAccount(@Param("searchTerm") String searchTerm, @Param("isActive") boolean isActive, @Param("accountId") Account accountId, Pageable pageable);

    Page<Notice> findByIsActiveAndAccount(boolean isActive, Account accountId, Pageable pageable);

    //활성화 공지
    //관리자 공지 활성화된거
    @Query("SELECT n FROM Notice n " +
            "JOIN n.account a " +
            "WHERE n.isActive = true " +
            "ORDER BY a.role ASC, n.regTime DESC")
    Page<Notice> findActiveNoticesWithAccountsOrdered(Pageable pageable);

    //제목으로 검색
    @Query("SELECT n FROM Notice n " +
            "JOIN n.account a " +
            "WHERE n.isActive = true " +
            "AND (n.title LIKE %:title%) " +
            "ORDER BY a.role ASC, n.regTime DESC")
    Page<Notice> findActiveNoticesWithTitle(@Param("title") String title, Pageable pageable);

    //작성자로 검색
    @Query("SELECT n FROM Notice n " +
            "JOIN n.account a " +
            "WHERE n.isActive = true " +
            "AND (a.accountId LIKE %:searchTerm% OR a.name LIKE %:searchTerm%) " +
            "ORDER BY a.role ASC, n.regTime DESC")
    Page<Notice> findActiveNoticesWithAccount(@Param("searchTerm") String searchTerm,
                                                      Pageable pageable);
    //작성일로 검색(관리자)
    @Query("SELECT n FROM Notice n " +
            "JOIN n.account a " +
            "WHERE n.isActive = true " +
            "AND n.regTime BETWEEN :startDateTime AND :endDateTime " +
            "ORDER BY a.role ASC, n.regTime DESC")
    Page<Notice> findActiveNoticesWithinDateRange(@Param("startDateTime") LocalDateTime startDateTime,
                                                  @Param("endDateTime") LocalDateTime endDateTime,
                                                  Pageable pageable);

    //작성일로 검색(유저)
    @Query("SELECT n FROM Notice n " +
            "JOIN n.account a " +
            "WHERE n.regTime BETWEEN :startDateTime AND :endDateTime " +
            "AND n.isActive = :isActive " +
            "AND (n.account = :accountId OR a.role = 'ADMIN')" +
            "ORDER BY a.role ASC, n.regTime DESC")
    Page<Notice> findByDateRangeIsActiveAndAccount(@Param("startDateTime") LocalDateTime startDateTime,
                                                   @Param("endDateTime") LocalDateTime endDateTime,
                                                   @Param("isActive") boolean isActive,
                                                   @Param("accountId") Account accountId,
                                                   Pageable pageable);

    //유저 공지 활성화된거
    @Query("SELECT n FROM Notice n " +
            "JOIN n.account a " +
            "WHERE (a.role = 'admin' OR (a.role = 'user' AND a.accountId = :accountId)) " +
            "AND n.isActive = true " +
            "ORDER BY a.role ASC, n.regTime DESC")
    Page<Notice> findUserNoticesByRole(@Param("accountId") String accountId, Pageable pageable);

    //제목으로 검색
    @Query("SELECT n FROM Notice n " +
            "JOIN n.account a " +
            "WHERE (a.role = 'admin' OR (a.role = 'user' AND a.accountId = :accountId)) " +
            "AND n.isActive = true " +
            "AND (n.title LIKE %:searchTerm%) " +
            "ORDER BY a.role ASC, n.regTime DESC")
    Page<Notice> findUserNoticesByRoleWithTitle(@Param("accountId") String accountId, @Param("searchTerm") String searchTerm, Pageable pageable);



    //비활성화 공지
    //관리자 공지 비활성화 된거
    @Query("SELECT n FROM Notice n " +
            "JOIN n.account a " +
            "WHERE n.isActive = false " +
            "ORDER BY n.regTime DESC")
    Page<Notice> findDeActivationNoticesWithAccountsOrdered(Pageable pageable);

    //제목으로 검색
    @Query("SELECT n FROM Notice n " +
            "JOIN n.account a " +
            "WHERE n.isActive = false " +
            "AND (n.title LIKE %:searchTerm%) " +
            "ORDER BY n.regTime DESC")
    Page<Notice> findDeActivationNoticesWithTitle(@Param("searchTerm") String searchTerm, Pageable pageable);

    //등록일 검색(관리자)
    @Query("select n from Notice n "+
            "join n.account a "+
            "where n.regTime between :startDateTime and :endDateTime "+
            "and n.isActive = false "+
            "order by n.regTime DESC")
    Page<Notice> findDeActivationNoticesWithRegTimeAdmin(@Param("startDateTime") LocalDateTime startDateTime,
                                                    @Param("endDateTime") LocalDateTime endDateTime,
                                                    Pageable pageable);

    //등록일 검색(유저)
    @Query("select n from Notice n "+
            "join n.account a "+
            "where n.regTime between :startDateTime and :endDateTime "+
            "and a.accountId = :accountIdStr "+
            "and n.isActive = false "+
            "order by n.regTime DESC")
    Page<Notice> findDeActivationNoticesWithRegTimeUser(@Param("startDateTime") LocalDateTime startDateTime,
                                                    @Param("endDateTime") LocalDateTime endDateTime,
                                                    @Param("accountIdStr") String accountIdStr,
                                                    Pageable pageable);

    //작성자 검색
    @Query("SELECT n FROM Notice n " +
            "JOIN n.account a " +
            "WHERE n.isActive = false " +
            "AND (a.accountId LIKE %:searchTerm% OR a.name LIKE %:searchTerm%) " +
            "ORDER BY n.regTime DESC")
    Page<Notice> findDeActivationNoticesWithAccount(@Param("searchTerm") String searchTerm, Pageable pageable);


}