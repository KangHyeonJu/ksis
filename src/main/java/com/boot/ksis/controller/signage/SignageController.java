package com.boot.ksis.controller.signage;

import com.boot.ksis.dto.PlayListAddDTO;
import com.boot.ksis.dto.PlayListSequenceDTO;
import com.boot.ksis.dto.SignageFormDTO;
import com.boot.ksis.dto.SignageNoticeStatusDTO;
import com.boot.ksis.service.account.AccountListService;
import com.boot.ksis.service.signage.SignageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signage")
public class SignageController {
    private final SignageService signageService;
    private final AccountListService accountService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @GetMapping()
    public ResponseEntity<?> pcList(){
        return new ResponseEntity<>(signageService.getSignageList(), HttpStatus.OK);
    }

    @GetMapping("/new")
    public ResponseEntity<?> pcAdd(){
        return new ResponseEntity<>(accountService.getAccountList(), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<String> pcAddPost(@RequestPart("signageFormDto")SignageFormDTO signageFormDTO, @RequestPart(value="accountList",required = false) String accountListJson) throws JsonProcessingException {
        List<String> accountList = objectMapper.readValue(accountListJson, new TypeReference<List<String>>() {});

        signageFormDTO.setIsShow(false);
        signageService.saveNewSignage(signageFormDTO, accountList);

        return ResponseEntity.ok("재생장치가 정상적으로 등록되었습니다.");
    }

    @GetMapping("/{signageId}")
    public ResponseEntity<?> signageDtl(@PathVariable("signageId") Long signageId){
        try {
            return new ResponseEntity<>(signageService.getSignageDtl(signageId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }

    @GetMapping("/update/{signageId}")
    public ResponseEntity<?> signageDtlUpdate(@PathVariable("signageId") Long signageId){
        try {
            return new ResponseEntity<>(signageService.getSignageDtl(signageId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }

    @PutMapping("/update/{signageId}")
    public ResponseEntity<String> signagePut(@PathVariable Long signageId, @RequestBody SignageNoticeStatusDTO signageNoticeStatusDTO) {
        System.out.println("signageNoticeStatusDTO: " + signageNoticeStatusDTO.isShowNotice());

        signageService.updateSignageStatus(signageId, signageNoticeStatusDTO.isShowNotice());
        return ResponseEntity.ok("재생장치 공지표시 상태가 정상적으로 수정되었습니다.");
    }

    @PatchMapping("/update")
    public ResponseEntity<String> signageUpdate(@RequestPart("signageFormDto") SignageFormDTO signageFormDto, @RequestPart(value="accountList",required = false) String accountListJson) throws JsonProcessingException{
        List<String> accountList = objectMapper.readValue(accountListJson, new TypeReference<>() {});

        signageService.updateSignage(signageFormDto, accountList);
        return ResponseEntity.ok("재생장치가 정상적으로 수정되었습니다.");
    }

    @GetMapping("/notice/{signageId}")
    public ResponseEntity<?> signageNotice(@PathVariable("signageId") Long signageId){
        try {
            return new ResponseEntity<>(signageService.getSignageNotice(signageId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }

    @GetMapping("/resource/{signageId}")
    public ResponseEntity<?> signageResource(@PathVariable("signageId") Long signageId){
        try{
            return new ResponseEntity<>(signageService.getResourceList(signageId), HttpStatus.OK);
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }

    }

    @DeleteMapping("/resource/{signageId}/{encodedResourceId}")
    public ResponseEntity<?> deleteEncodedResource(@PathVariable("signageId") Long signageId, @PathVariable("encodedResourceId") Long encodedResourceId){
        try {
            signageService.deleteEncodedResource(signageId, encodedResourceId);

            return ResponseEntity.ok("Deleted successfully");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting");
        }
    }

    @GetMapping("/playlist/{signageId}")
    public ResponseEntity<?> signagePlaylistList(@PathVariable("signageId") Long signageId){
        try{
            return new ResponseEntity<>(signageService.getPlaylistList(signageId), HttpStatus.OK);
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }

    @PutMapping("/playlist/{signageId}")
    public ResponseEntity<String> selectPlaylist(@PathVariable("signageId") Long signageId, @RequestBody Map<String, Long> playlistId){
        Long selectedPlaylist = playlistId.get("selectedPlaylist");
        signageService.setPlaylist(signageId, selectedPlaylist);

        return ResponseEntity.ok("Playlist selected successfully");
    }

    @GetMapping("/playlist")
    public ResponseEntity<?> playlistDtl(@RequestParam Long playlistDtlId){
        try{
            return new ResponseEntity<>(signageService.getPlaylistDtl(playlistDtlId), HttpStatus.OK);
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생목록입니다.", HttpStatus.OK);
        }
    }

    @DeleteMapping("/playlist")
    public ResponseEntity<?> deletePlaylist(@RequestParam Long playlistId){
        try {
            signageService.deletePlaylist(playlistId);

            return ResponseEntity.ok("Deleted successfully");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting");
        }
    }

    @PostMapping("/playlist")
    public ResponseEntity<String> addPlaylist(@RequestPart("playListAddDTO") PlayListAddDTO playListAddDTO, @RequestPart("resourceSequence") List<PlayListSequenceDTO> resourceSequence){
        signageService.addPlaylist(playListAddDTO, resourceSequence);

        return ResponseEntity.ok("재생목록이 정상적으로 등록되었습니다.");
    }

    @GetMapping("/playlistDtl/{playListId}")
    public ResponseEntity<?> getPlaylistDtl(@PathVariable("playListId") Long playListId){
        try{
            return new ResponseEntity<>(signageService.playListDtl(playListId), HttpStatus.OK);
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생목록입니다.", HttpStatus.OK);
        }
    }

    @PutMapping("/playlistDtl/{playListId}")
    public ResponseEntity<String> updatePlaylist(@PathVariable("playListId") Long playListId, @RequestPart("playListAddDTO") PlayListAddDTO playListAddDTO, @RequestPart("resourceSequence") List<PlayListSequenceDTO> resourceSequence){
        signageService.resourceSequence(playListId, playListAddDTO, resourceSequence);

        return ResponseEntity.ok("재생목록이 정상적으로 수정되었습니다.");
    }
}
