package com.boot.ksis.entity.Log;

import com.boot.ksis.entity.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "activity_log")
@Getter
@Setter
public class ActivityLog extends BaseEntity {
    //액티비티
    @Id
    @Column(name = "activity_log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityLogId;

    private String activityDetail;
}
