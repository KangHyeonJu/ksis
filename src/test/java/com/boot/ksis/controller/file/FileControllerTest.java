package com.boot.ksis.controller.file;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boot.ksis.service.file.FileBoardService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private FileBoardService fileBoardService;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // 파일 비활성화 테스트
    @Test
    public void testDeactivationFile() throws Exception {
        Long originalResourceId = 1L;

        // fileBoardService.deactivationFile()가 호출될 때 아무 동작도 하지 않도록 설정
        doNothing().when(fileBoardService).deactivationFile(originalResourceId);

        mockMvc.perform(post("/deactivation/" + originalResourceId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());  // 비활성화 후 204 응답

        // 서비스 메서드 호출 여부 검증
        verify(fileBoardService, times(1)).deactivationFile(originalResourceId);
    }

    // 인코딩 파일 삭제 테스트
    @Test
    public void testDeleteEncodedFile() throws Exception {
        Long encodedResourceId = 2L;

        // fileBoardService.deleteEncodedFile()가 호출될 때 아무 동작도 하지 않도록 설정
        doNothing().when(fileBoardService).deleteEncodedFile(encodedResourceId);

        mockMvc.perform(delete("/encoded/" + encodedResourceId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());  // 삭제 후 204 응답

        // 서비스 메서드 호출 여부 검증
        verify(fileBoardService, times(1)).deleteEncodedFile(encodedResourceId);
    }
}
