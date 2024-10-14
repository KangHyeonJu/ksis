package com.boot.ksis.controller.resolution;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.resolution.ResolutionDTO;
import com.boot.ksis.service.resolution.ResolutionService;
import jakarta.persistence.EntityNotFoundException;
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
    public ResponseEntity<?> resolutionList(@RequestParam int page,
                                                              @RequestParam int size,
                                                              @RequestParam(required = false) String searchTerm,
                                                              @RequestParam(required = false) String searchCategory){
        try{
            return new ResponseEntity<>(resolutionService.getResolutionList(page, size, searchTerm, searchCategory), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("해상도 조회에 실패했습니다.", HttpStatus.OK);
        }
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

    @GetMapping("/{resolutionId}")
    public ResponseEntity<?> getResolution(@PathVariable("resolutionId") Long resolutionId){
        try {
            return new ResponseEntity<>(resolutionService.getResolution(resolutionId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않습니다.", HttpStatus.OK);
        }
    }

    @CustomAnnotation(activityDetail = "해상도 수정")
    @PutMapping()
    public ResponseEntity<String> updateResolution(@RequestBody ResolutionDTO resolutionDTO){
        try{
            resolutionService.updateResolution(resolutionDTO);
            return ResponseEntity.ok("해상도가 정상적으로 등록되었습니다.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error post resolution");
        }
    }
}
