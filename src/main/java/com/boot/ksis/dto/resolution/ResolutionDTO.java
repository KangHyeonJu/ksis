package com.boot.ksis.dto.resolution;

import com.boot.ksis.entity.Resolution;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter @Setter @Builder
public class ResolutionDTO {
    //해상도 id
    private Long resolutionId;

    //해상도 이름
    private String name;

    //가로(너비) 픽셀 수
    private int width;

    //세로(높이) 픽셀 수
    private int height;

    private static ModelMapper modelMapper = new ModelMapper();

    public Resolution postResolution(){
        return modelMapper.map(this, Resolution.class);
    }
}
