package com.boot.ksis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "upload_log")
@Getter
@Setter
public class UploadLog extends BaseEntity{
    //업로드 로그 id
    @Id
    @Column(name = "upload_log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uploadId;

    //인코딩 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encoded_resource_id")
    private EncodedResource encodedResource;
}
