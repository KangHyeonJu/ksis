package com.boot.ksis.entity.Log;

import com.boot.ksis.constant.Category;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "access_log")
@Getter
@Setter
public class AccessLog extends BaseEntity {
    //엑세스 로그 id
    @Id
    @Column(name = "access_log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accessLogId;

    //계정 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    //접근카테고리
    private Category category;
}
