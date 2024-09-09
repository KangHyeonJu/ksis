package com.boot.ksis.service.visit;

import com.boot.ksis.dto.visit.VisitDTO;
import com.boot.ksis.repository.account.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    public List<VisitDTO> getVisitCount(){
        //오늘 포함 30일, 각 일에 방문자수 카운트
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        List<Object[]> objects =  visitRepository.countVisitsByDateBetween(startDate, endDate);

        List<VisitDTO> visitDTOList = new ArrayList<>();

        for(Object[] object : objects){
            VisitDTO visitDTO = VisitDTO.builder().x((LocalDate) object[0]).y((Long) object[1]).build();
            visitDTOList.add(visitDTO);
        }
        return visitDTOList;
    }
}
