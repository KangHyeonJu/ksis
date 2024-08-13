package com.boot.ksis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ResourceDTO {
    private String filename;
    private String title;
    private String filePath;
    private long playTime;
    private String format;
    private String resolution;
    private long fileSize;
    private String status;
}
