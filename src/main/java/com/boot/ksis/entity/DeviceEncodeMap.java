package com.boot.ksis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "device_encode_map")
@IdClass(DeviceEncodeId.class)
@Getter
@Setter
public class DeviceEncodeMap {
    //디바이스 id
    @Id
    private Long deviceId;

    //인코딩 id
    @Id
    private Long encodedResourceId;

    @ManyToOne
    @MapsId("deviceId")
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne
    @MapsId("encodedResourceId")
    @JoinColumn(name = "encoded_resource_id")
    private EncodedResource encodedResource;
}
