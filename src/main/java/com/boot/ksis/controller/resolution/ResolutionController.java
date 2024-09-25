package com.boot.ksis.controller.resolution;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.resolution.ResolutionDTO;
import com.boot.ksis.service.resolution.ResolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resolution")
public class ResolutionController {
    private final ResolutionService resolutionService;

    @GetMapping()
    public ResponseEntity<List<ResolutionDTO>> resolutionList(){
        return new ResponseEntity<>(resolutionService.getResolutionList(), HttpStatus.OK);
    }

    @CustomAnnotation(activityDetail = "해상도 등록")
    @PostMapping()
    public ResponseEntity<String> postResolution(@RequestBody ResolutionDTO resolutionDTO){
        try{
            resolutionService.postResolution(resolutionDTO);
            return ResponseEntity.ok("해상도가 정상적으로 등록되었습니다.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error post resolution");
        }
    }

    @CustomAnnotation(activityDetail = "해상도 삭제")
    @DeleteMapping()
    public ResponseEntity<?> deleteResolutionList(@RequestParam List<Long> resolutionIds){
        try{
            resolutionService.deleteResolutionList(resolutionIds);
            return ResponseEntity.ok("Resolutions delete successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting Resolutions");
        }
    }
}
