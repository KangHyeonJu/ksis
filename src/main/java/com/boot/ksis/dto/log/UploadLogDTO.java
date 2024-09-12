package com.boot.ksis.dto.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class UploadLogDTO {

    private String accountId;
    private String message;
}
