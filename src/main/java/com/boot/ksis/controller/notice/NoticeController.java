package com.boot.ksis.controller.notice;

import com.boot.ksis.dto.NoticeDTO;
import com.boot.ksis.dto.NoticeFormDTO;
import com.boot.ksis.service.notice.NoticeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = "http://localhost:3000")
public class NoticeController {

}