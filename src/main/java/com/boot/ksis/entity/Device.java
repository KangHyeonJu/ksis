package com.boot.ksis.entity;

import com.boot.ksis.constant.DeviceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "device")
@Getter
@Setter
public class Device extends BaseEntity{

    //디바이스 id
    @Id
    @Column(name = "device_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deviceId;

    //mac 주소
    @Column(nullable = false, length = 50)
    private String macAddress;

    //디바이스명
    @Column(nullable = false, length = 50)
    private String deviceName;

    //위치
    @Column(nullable = false, length = 100)
    private String location;

    //크기
    @Column(nullable = false, length = 50)
    private String screenSize;

    //해상도
    @Column(nullable = false, length = 50)
    private String resolution;

    //상세주소
    @Column(nullable = false, length = 100)
    private String detailAddress;

    //공지표시여부
    @Column(nullable = false, columnDefinition = "TINYINT(0)")
    @ColumnDefault("false") //공지표시 O
    private boolean isShow;

    //디바이스 유형
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;
}
