package com.boot.ksis.dto;

import com.boot.ksis.entity.Device;
import com.boot.ksis.entity.PlayList;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Setter
@Getter
public class PlayListAddDTO {
    //디바이스 아이디
    private Long deviceId;

    //재생목록 제목
    private String fileTitle;

    //슬라이드 시간
    private int slideTime;

    private static ModelMapper modelMapper = new ModelMapper();

    public PlayList createNewSignage(Device device){
        PlayList playList = modelMapper.map(this, PlayList.class);
        playList.setIsDefault(false);
        playList.setDevice(device);

        return playList;
    }
}
