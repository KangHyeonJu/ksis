package com.boot.ksis.dto.resource;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class OriginalResourceDTO {
    private String filename;
    private String title;
    private String filePath;
    private float playTime;
    private String format;
    private String resolution;
    private long fileSize;
    private ResourceStatus status;
    private ResourceType resourceType;

}
