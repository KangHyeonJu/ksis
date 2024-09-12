package com.boot.ksis.entity.Log;

import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Base.BaseEntity;
import com.boot.ksis.entity.EncodedResource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "upload_log")
@Getter
@Setter
public class UploadLog extends BaseEntity {
    //업로드 로그 id
    @Id
    @Column(name = "upload_log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uploadId;

    //메시지
    @Column(name = "message")
    private String message;

    //계정 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
}
