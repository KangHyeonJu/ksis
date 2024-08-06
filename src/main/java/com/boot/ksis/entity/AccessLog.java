package com.boot.ksis.entity;

import com.boot.ksis.constant.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "access_log")
@Getter
@Setter
public class AccessLog extends BaseEntity{
    //엑세스 로그 id
    @Id
    @Column(name = "access_log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accessLogId;

    private Category category;
}
