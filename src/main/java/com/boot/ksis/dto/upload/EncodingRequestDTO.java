package com.boot.ksis.dto.upload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EncodingRequestDTO {
    private List<Map<String, String>> encodings;
    private String title;

    private String accountId;
}
