package com.boot.ksis.entity.Log;

import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "activity_log")
@Getter
@Setter
@NoArgsConstructor
public class ActivityLog extends BaseEntity {
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

    @Builder
    public ActivityLog(Account account, String activityDetail){
        this.account = account;
        this.activityDetail = activityDetail;
    }
}
