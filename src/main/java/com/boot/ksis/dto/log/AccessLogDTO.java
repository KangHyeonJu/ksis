package com.boot.ksis.dto.log;

import com.boot.ksis.constant.Category;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccessLogDTO {
    private Long accountId;
    private Category category;
}
