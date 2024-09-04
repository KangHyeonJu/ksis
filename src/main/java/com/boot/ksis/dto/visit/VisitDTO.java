package com.boot.ksis.dto.visit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@Builder
public class VisitDTO {
    //날짜
    private LocalDate x;

    //방문자수
    private Long y;
}
