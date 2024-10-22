/*
package com.boot.ksis.controller;

import com.boot.ksis.constant.Role;
import com.boot.ksis.controller.file.FileBoardController;
import com.boot.ksis.dto.file.ResourceListDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.service.file.FileBoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileBoardController.class)
public class FileBoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private FileBoardService fileBoardService;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private FileBoardController fileBoardController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(fileBoardController).build();
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testGetDeactiveRsImageFiles_Success() throws Exception {
        // 가짜 계정과 역할 설정
        Account mockAccount = new Account();
        mockAccount.setAccountId("testUser");
        mockAccount.setRole(Role.USER);

        // 가짜 계정 데이터 설정
        when(accountRepository.findById(anyString())).thenReturn(Optional.of(mockAccount));

        // 가짜 비활성화된 이미지 파일 리스트 설정
        List<ResourceListDTO> mockImageFiles = new ArrayList<>();
        mockImageFiles.add(new ResourceListDTO(1L, "thumbnail_path", "file_path", "file_title", "resolution", "format", LocalDateTime.now()));

        when(fileBoardService.getDeactiveImageFiles(any(Account.class), any(Role.class))).thenReturn(mockImageFiles);

        // 요청을 보내고 상태 및 응답 데이터 확인
        mockMvc.perform(get("/deactive/Images")
                        .principal(() -> "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].originalResourceId").value(1L))
                .andExpect(jsonPath("$[0].thumbNailPath").value("thumbnail_path"))
                .andExpect(jsonPath("$[0].filePath").value("file_path"))
                .andExpect(jsonPath("$[0].fileTitle").value("file_title"))
                .andExpect(jsonPath("$[0].resolution").value("resolution"))
                .andExpect(jsonPath("$[0].format").value("format"));

        verify(accountRepository, times(1)).findById("testUser");
        verify(fileBoardService, times(1)).getDeactiveImageFiles(any(Account.class), any(Role.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testGetDeactiveRsImageFiles_AccountNotFound() throws Exception {
        when(accountRepository.findById(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/deactive/Images")
                        .principal(() -> "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("계정 정보를 찾을 수 없습니다."));

        verify(accountRepository, times(1)).findById("testUser");
        verify(fileBoardService, never()).getDeactiveImageFiles(any(Account.class), any(Role.class));
    }

    @Test
    public void testGetDeactiveRsImageFiles_Unauthenticated() throws Exception {
        mockMvc.perform(get("/deactive/Images")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value("사용자가 인증되지 않았습니다."));
    }
}
*/
