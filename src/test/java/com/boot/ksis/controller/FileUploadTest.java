/*
package com.boot.ksis.controller;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.upload.OriginalResourceDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FileUploadTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSaveOriginalResource() throws Exception {
        // Mock 파일 생성
        MockMultipartFile mockFile = new MockMultipartFile("files", "example.jpg", "image/jpeg", "test image content".getBytes());

        // DTO 리스트 생성
        List<OriginalResourceDTO> dtos = new ArrayList<>();
        OriginalResourceDTO dto = new OriginalResourceDTO();
        dto.setFileTitle("Example File");
        dto.setFilePath("/uploads/images");
        dto.setPlayTime(300.0f);
        dto.setFormat("jpg");
        dto.setResolution("1920x1080");
        dto.setFileSize(1024);
        dto.setStatus(ResourceStatus.UPLOADING);
        dto.setResourceType(ResourceType.IMAGE);
        dtos.add(dto);

        // JSON 형식으로 DTO 직렬화
        String dtosJson = new ObjectMapper().writeValueAsString(dtos);

        // DTO를 JSON으로 전달하는 MockMultipartFile 생성
        MockMultipartFile dtosFile = new MockMultipartFile("dtos", "", "application/json", dtosJson.getBytes());

        // MockMvc를 통해 파일과 DTO 전송
        mockMvc.perform(multipart("/api/filedatasave")
                        .file(mockFile)
                        .file(dtosFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileTitle").value("Example File"))
                .andExpect(jsonPath("$[0].filePath").value(startsWith("/file/uploads")))
                .andExpect(jsonPath("$[0].filePath").value(containsString(".jpg")));
    }

}
*/
