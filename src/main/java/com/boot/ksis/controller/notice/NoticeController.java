package com.boot.ksis.controller.notice;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.constant.Role;
import com.boot.ksis.dto.notice.DetailNoticeDTO;
import com.boot.ksis.dto.notice.NoticeDTO;
import com.boot.ksis.dto.notice.UpdateNoticeDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final AccountRepository accountRepository;


    // 공지 등록
    @CustomAnnotation(activityDetail = "공지 등록")
    @PostMapping
    public ResponseEntity<String> createNotice(Principal principal, @RequestBody NoticeDTO noticeDTO) {
        String accountId = principal.getName();

        noticeDTO.setAccountId(accountId);
        noticeDTO.setName(noticeDTO.getName());
        //공지 등록
        noticeService.createNotice(noticeDTO);
        return ResponseEntity.ok("공지가 정상적으로 등록되었습니다.");
    }

    // 공지 수정
    @PutMapping("/{noticeId}")
    public ResponseEntity<String> updateNotice(Principal principal, @PathVariable Long noticeId, @RequestBody UpdateNoticeDTO updateNoticeDTO) {
        String accountId = principal.getName();

        updateNoticeDTO.setAccountId(accountId);

        noticeService.updateNotice(noticeId, updateNoticeDTO);
        return ResponseEntity.ok("공지사항이 성공적으로 수정되었습니다.");
    }

    // 공지 비활성화
    @PostMapping("/deactivation/{noticeId}")
    public ResponseEntity<?> DeactivationNotice(@PathVariable Long noticeId) {
        noticeService.DeactivationNotice(noticeId);
        return ResponseEntity.ok("공지사항이 성공적으로 삭제되었습니다.");
    }

    // 공지 조회 (본인 및 관리자 공지 전체)
    @GetMapping("/all")
    public ResponseEntity<?> getUserNotices(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>("계정 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        Account account = accountOptional.get();
        Role role = account.getRole();

        if (role == null) {
            return new ResponseEntity<>("역할 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (role.equals(Role.ADMIN)) { // Role 객체와 비교
            return new ResponseEntity<>(noticeService.getAllActiveNotices(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(noticeService.getUserActiveNotices(accountId), HttpStatus.OK);
        }
    }

    // 공지 조회 (본인 및 관리자 공지 전체)
    @GetMapping("/deactivation/all")
    public ResponseEntity<?> getDeactivationNotices(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>("계정 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        Account account = accountOptional.get();
        Role role = account.getRole();

        if (role == null) {
            return new ResponseEntity<>("역할 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (role.equals(Role.ADMIN)) { // Role 객체와 비교
            return new ResponseEntity<>(noticeService.getAllNoneActiveNotices(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(noticeService.getUserNoneActiveNotices(accountId), HttpStatus.OK);
        }
    }


    // 공지 상세조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<?> getNoticeById(@PathVariable Long noticeId) {
        try {
            // 공지 상세 조회 서비스 호출
            DetailNoticeDTO notice = noticeService.getActiveNoticeById(noticeId);
            return ResponseEntity.ok(notice); // 성공 시 상세 공지 반환

        } catch (RuntimeException e) {
            // 비활성화된 공지일 경우 사용자에게 메시지 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 공지는 비활성화된 상태입니다.");
        }
    }
}