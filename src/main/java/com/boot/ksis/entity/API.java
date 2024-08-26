package com.boot.ksis.entity;

import com.boot.ksis.entity.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "api")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class API extends BaseEntity {
    //api id
    @Id
    @Column(name = "api_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long apiId;

    //api 이름
    @Column(nullable = false, length = 50)
    private String apiName;

    //제공업체
    @Column(nullable = false, length = 50)
    private String provider;

    //key
    private String keyValue;

    //만료일
    private LocalDateTime expiryDate;

    //사용 목적
    private String purpose;
}
