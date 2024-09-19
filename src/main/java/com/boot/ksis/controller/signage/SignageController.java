package com.boot.ksis.controller.signage;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.playlist.PlayListAddDTO;
import com.boot.ksis.dto.playlist.PlayListSequenceDTO;
import com.boot.ksis.dto.signage.SignageFormDTO;
import com.boot.ksis.dto.signage.SignageNoticeStatusDTO;
import com.boot.ksis.service.account.AccountListService;
import com.boot.ksis.service.signage.SignageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signage")
public class SignageController {
    private final SignageService signageService;
    private final AccountListService accountService;

    //재생장치 목록 조회
    @GetMapping()
    public ResponseEntity<?> signageList(Principal principal, @RequestParam String role){
        //현재 로그인한 id 가져오기
        String accountId = principal.getName();

        if(role.contains("ADMIN")){     //관리자일 경우 전체 목록 조회
            return new ResponseEntity<>(signageService.getSignageAll(), HttpStatus.OK);
        }else{      //USER일 경우 해당 USER가 담당자인 재생장치만 조회
            return new ResponseEntity<>(signageService.getSignageList(accountId), HttpStatus.OK);
        }
    }

    //재생장치 목록 조회 - 그리드 형태
    @GetMapping("/grid")
    public  ResponseEntity<?> signageGridList(Principal principal, @RequestParam String role){
        //현재 로그인한 id 가져오기
        String accountId = principal.getName();

        if(role.contains("ADMIN")){     //관리자일 경우 전체 목록 조회
            return new ResponseEntity<>(signageService.getSignageGridAll(), HttpStatus.OK);
        }else{      //USER일 경우 해당 USER가 담당자인 재생장치만 조회
            return new ResponseEntity<>(signageService.getSignageGridList(accountId), HttpStatus.OK);
        }
    }

    //재생장치 등록 시 USER 목록 조회
    @GetMapping("/account")
    public ResponseEntity<?> signageAdd(){
        return new ResponseEntity<>(accountService.getAccountList(), HttpStatus.OK);
    }

    //재생장치 상세 조회
    @GetMapping("/{signageId}")
    public ResponseEntity<?> signageDtl(@PathVariable("signageId") Long signageId){
        try {
            return new ResponseEntity<>(signageService.getSignageDtl(signageId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }

    //재생장치 정보 수정 시 기존 정보 조회
    @GetMapping("/update/{signageId}")
    public ResponseEntity<?> signageDtlUpdate(@PathVariable("signageId") Long signageId){
        try {
            return new ResponseEntity<>(signageService.getSignageDtl(signageId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }

    //재생장치 공지 표시 여부
    @CustomAnnotation(activityDetail = "재생장치 공지표시여부 수정")
    @PutMapping("/update/{signageId}")
    public ResponseEntity<String> signagePut(@PathVariable Long signageId, @RequestBody SignageNoticeStatusDTO signageNoticeStatusDTO) {
        signageService.updateSignageStatus(signageId, signageNoticeStatusDTO.isShowNotice());
        return ResponseEntity.ok("재생장치 공지표시 상태가 정상적으로 수정되었습니다.");
    }

    //재생장치 정보 수정
    @CustomAnnotation(activityDetail = "재생장치 수정")
    @PatchMapping("/update")
    public ResponseEntity<String> signageUpdate(@RequestPart("signageFormDto") SignageFormDTO signageFormDto,  @RequestPart(value="accountList") List<String> accountList){
        //MAC 주소 중복 검증
        if(signageService.checkUpdateMacAddress(signageFormDto)){
            signageService.updateSignage(signageFormDto, accountList);
            return ResponseEntity.ok("재생장치가 정상적으로 수정되었습니다.");
        }else {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("이미 등록된 MAC주소입니다.");
        }
    }

    //재생장치에 해당하는 공지 조회
    @GetMapping("/notice/{signageId}")
    public ResponseEntity<?> signageNotice(@PathVariable("signageId") Long signageId){
        try {
            return new ResponseEntity<>(signageService.getSignageNotice(signageId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }

    //재생장치에 등록된 파일 조회
    @GetMapping("/resource/{signageId}")
    public ResponseEntity<?> signageResource(@PathVariable("signageId") Long signageId){
        try{
            return new ResponseEntity<>(signageService.getResourceList(signageId), HttpStatus.OK);
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }

    }

    //본인이 업로드한 파일 조회
    @GetMapping("/accountResource")
    public ResponseEntity<?> accountResource(Principal principal){
        try{
            //현재 로그인한 id 가져오기
            String accountId = principal.getName();

            return new ResponseEntity<>(signageService.getAccountResourceList(accountId), HttpStatus.OK);
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("resource를 찾을 수 없습니다.", HttpStatus.OK);
        }
    }

    //재생장치에 파일 등록
    @CustomAnnotation(activityDetail = "재생장치 resource 추가")
    @PostMapping("/resource/add/{signageId}")
    public ResponseEntity<?> addSignageResource(@PathVariable("signageId") Long signageId, @RequestPart(value="encodedResourceIdList") List<Long> encodedResourceIdList){
        signageService.addSignageResource(signageId, encodedResourceIdList);

        return ResponseEntity.ok("encodedResource 정상적으로 등록되었습니다.");
    }

    //재생장치에 등록된 파일 삭제
    @CustomAnnotation(activityDetail = "재생장치 resource 삭제")
    @DeleteMapping("/resource/{signageId}/{encodedResourceId}")
    public ResponseEntity<?> deleteEncodedResource(@PathVariable("signageId") Long signageId, @PathVariable("encodedResourceId") Long encodedResourceId){
        try {
            signageService.deleteEncodedResource(signageId, encodedResourceId);

            return ResponseEntity.ok("Deleted successfully");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting");
        }
    }

    //재생장치에 등록된 재생목록 조회
    @GetMapping("/playlist/{signageId}")
    public ResponseEntity<?> signagePlaylistList(@PathVariable("signageId") Long signageId){
        try{
            return new ResponseEntity<>(signageService.getPlaylistList(signageId), HttpStatus.OK);
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }

    //재생장치에서 재생할 재생목록 선택
    @CustomAnnotation(activityDetail = "재생목록 선택")
    @PutMapping("/playlist/{signageId}")
    public ResponseEntity<String> selectPlaylist(@PathVariable("signageId") Long signageId, @RequestBody Map<String, Long> playlistId){
        Long selectedPlaylist = playlistId.get("selectedPlaylist");
        signageService.setPlaylist(signageId, selectedPlaylist);

        return ResponseEntity.ok("Playlist selected successfully");
    }

    //재생목록 상세 조회
    @GetMapping("/playlist")
    public ResponseEntity<?> playlistDtl(@RequestParam Long playlistDtlId){
        try{
            return new ResponseEntity<>(signageService.getPlaylistDtl(playlistDtlId), HttpStatus.OK);
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생목록입니다.", HttpStatus.OK);
        }
    }

    //재생목록 삭제
    @CustomAnnotation(activityDetail = "재생목록 삭제")
    @DeleteMapping("/playlist")
    public ResponseEntity<?> deletePlaylist(@RequestParam Long playlistId){
        try {
            signageService.deletePlaylist(playlistId);

            return ResponseEntity.ok("Deleted successfully");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting");
        }
    }

    //재생목록 등록
    @CustomAnnotation(activityDetail = "재생목록 추가")
    @PostMapping("/playlist")
    public ResponseEntity<String> addPlaylist(@RequestPart("playListAddDTO") PlayListAddDTO playListAddDTO, @RequestPart("resourceSequence") List<PlayListSequenceDTO> resourceSequence){
        signageService.addPlaylist(playListAddDTO, resourceSequence);

        return ResponseEntity.ok("재생목록이 정상적으로 등록되었습니다.");
    }

    //재생목록 수정 시 기존 정보 조회
    @GetMapping("/playlistDtl/{playListId}")
    public ResponseEntity<?> getPlaylistDtl(@PathVariable("playListId") Long playListId){
        try{
            return new ResponseEntity<>(signageService.playListDtl(playListId), HttpStatus.OK);
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생목록입니다.", HttpStatus.OK);
        }
    }

    //재생목록 수정
    @CustomAnnotation(activityDetail = "재생목록 수정")
    @PutMapping("/playlistDtl/{playListId}")
    public ResponseEntity<String> updatePlaylist(@PathVariable("playListId") Long playListId, @RequestPart("playListAddDTO") PlayListAddDTO playListAddDTO, @RequestPart("resourceSequence") List<PlayListSequenceDTO> resourceSequence){
        signageService.resourceSequence(playListId, playListAddDTO, resourceSequence);

        return ResponseEntity.ok("재생목록이 정상적으로 수정되었습니다.");
    }


    //재생목록 재생 - 파일 정보
    @GetMapping("/play/{signageId}")
    public ResponseEntity<?> getPlaylistPlay(@PathVariable("signageId") Long signageId){
        try{
            return new ResponseEntity<>(signageService.getPlaylistPlay(signageId), HttpStatus.OK);
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("재생 시 오류가 발생했습니다.", HttpStatus.OK);
        }
    }

    //재생목록 재생 - 공지 정보
    @GetMapping("/play/notice/{signageId}")
    public ResponseEntity<?> getPlayNotice(@PathVariable("signageId") Long signageId){
        try{
            return new ResponseEntity<>(signageService.getPlayNotice(signageId), HttpStatus.OK);
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("재생 시 오류가 발생했습니다.", HttpStatus.OK);
        }
    }
}
