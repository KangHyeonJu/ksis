package com.boot.ksis.entity.Log;

import com.boot.ksis.entity.Account;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
@Getter
@Setter
@NoArgsConstructor
public class ActivityLog{
    //액티비티
    @Id
    @Column(name = "activity_log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityLogId;

    //계정 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    //활동기록
    private String activityDetail;

    //활동시간
    private LocalDateTime dateTime;

    @Builder
    public ActivityLog(Account account, String activityDetail, LocalDateTime dateTime){
        this.account = account;
        this.activityDetail = activityDetail;
        this.dateTime = dateTime;
    }
}
