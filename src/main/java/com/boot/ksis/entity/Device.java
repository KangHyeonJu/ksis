package com.boot.ksis.entity;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.dto.pc.PcFormDTO;
import com.boot.ksis.dto.signage.SignageFormDTO;
import com.boot.ksis.entity.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "device")
@Getter
@Setter
@DynamicInsert
public class Device extends BaseEntity {

    //디바이스 id
    @Id
    @Column(name = "device_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deviceId;

    //mac 주소
    @Column(length = 50)
    private String macAddress;

    //IP
    @Column(length = 50)
    private String ipAddress;

    //key
    @Column(length = 30)
    private String signageKey;

    //디바이스명
    @Column(nullable = false, length = 50)
    private String deviceName;

    //위치
    @Column(nullable = false, length = 100)
    private String location;

    //크기
    @Column(length = 50)
    private String screenSize;

    //해상도
    @Column(length = 50)
    private String resolution;

    //상세주소
    @Column(nullable = false, length = 100)
    private String detailAddress;

    //공지표시여부
    @Column(columnDefinition = "TINYINT(0)")
    @ColumnDefault("true") //공지표시 O
    private Boolean isShow;

    //디바이스 유형
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    //접속 상태
    @Column(columnDefinition = "TINYINT(0)")
    @ColumnDefault("0")
    private Boolean isConnect;

    public void updatePc(PcFormDTO pcFormDTO){
        this.deviceName = pcFormDTO.getDeviceName();
        this.location = pcFormDTO.getLocation();
        this.detailAddress = pcFormDTO.getDetailAddress();
        this.macAddress = pcFormDTO.getMacAddress();
    }

    public void updateSignage(SignageFormDTO signageFormDTO) {
        this.deviceName = signageFormDTO.getDeviceName();
        this.location = signageFormDTO.getLocation();
        this.detailAddress = signageFormDTO.getDetailAddress();
        this.ipAddress = signageFormDTO.getIpAddress();
        this.screenSize = signageFormDTO.getScreenSize();
        this.resolution = signageFormDTO.getResolution();
    }
}
