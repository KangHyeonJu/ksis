package com.boot.ksis.entity.IdClass;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@Setter
public class DeviceEncodeId implements Serializable {
    //디바이스 id
    private Long deviceId;

    //인코딩 id
    private Long encodedResourceId;
}
