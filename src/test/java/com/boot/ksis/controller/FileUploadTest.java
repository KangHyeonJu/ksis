package com.boot.ksis.controller;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.upload.OriginalResourceDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "USER")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileUploadTest {

    @Autowired
    private MockMvc mockMvc;

    private String generatedUUID; // 생성된 UUID를 저장할 필드
    private static final int CHUNK_SIZE = 1 * 1024 * 1024; // 청크 사이즈를 1MB로 설정

    private File testVideoFile; // 실제 파일을 사용할 변수

    @BeforeAll
    public void setup() throws Exception {
        // 테스트용으로 사용할 실제 파일 경로 설정
        Path filePath = Path.of("C:/ksis-file/test/219862.mp4"); // 경로 수정 필요
        testVideoFile = filePath.toFile();

        if (!testVideoFile.exists()) {
            throw new Exception("테스트용 파일을 찾을 수 없습니다: " + filePath.toString());
        }
    }

    @Test
    @Order(1)
    public void testSaveOriginalResource() throws Exception {
        // 실제 파일로부터 MultipartFile 생성
        MockMultipartFile mockFile = new MockMultipartFile("files", testVideoFile.getName(), "video/mp4", new FileInputStream(testVideoFile));

        // DTO 리스트 생성
        List<OriginalResourceDTO> dtos = new ArrayList<>();
        OriginalResourceDTO dto = new OriginalResourceDTO();
        dto.setAccount("admin");
        dto.setFileTitle("Test Video File");
        dto.setFilePath("/uploads/videos");
        dto.setPlayTime(300.0f);
        dto.setFormat("mp4");
        dto.setResolution("1920x1080");
        dto.setFileSize(testVideoFile.length());
        dto.setStatus(ResourceStatus.UPLOADING);
        dto.setResourceType(ResourceType.VIDEO);
        dtos.add(dto);

        // JSON 형식으로 DTO 직렬화
        String dtosJson = new ObjectMapper().writeValueAsString(dtos);

        // DTO를 JSON으로 전달하는 MockMultipartFile 생성
        MockMultipartFile dtosFile = new MockMultipartFile("dtos", "", "application/json", dtosJson.getBytes());

        // MockMvc를 통해 파일과 DTO 전송
        mockMvc.perform(multipart("/api/filedatasave/admin")
                        .file(mockFile)
                        .file(dtosFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileTitle").value("Test Video File"))
                .andExpect(jsonPath("$[0].filePath").value(startsWith("/file/uploads")))
                .andExpect(jsonPath("$[0].filePath").value(containsString(".mp4")))
                .andDo(result -> {
                    // JSON 응답에서 생성된 파일 경로를 추출하여 UUID를 저장
                    String responseContent = result.getResponse().getContentAsString();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonResponse = objectMapper.readTree(responseContent);

                    String filePath = jsonResponse.get(0).get("filePath").asText();
                    generatedUUID = filePath.substring(filePath.lastIndexOf("/") + 1); // UUID 추출
                });
    }

    @Test
    @Order(2)
    public void testChunkUploadResume() throws Exception {
        // 파일의 전체 사이즈 계산
        long totalSize = testVideoFile.length();
        int totalChunks = (int) Math.ceil((double) totalSize / CHUNK_SIZE); // 총 청크 수 계산

        // 파일 메타데이터가 저장된 후에 UUID 값이 존재하는지 확인
        if (generatedUUID == null || generatedUUID.isEmpty()) {
            throw new IllegalStateException("UUID가 생성되지 않았습니다. 먼저 testSaveOriginalResource 테스트를 실행하세요.");
        }

        // 청크별로 파일 업로드
        try (FileInputStream fis = new FileInputStream(testVideoFile)) {
            byte[] buffer = new byte[CHUNK_SIZE];

            // 첫 50% 청크 업로드
            for (int chunkIndex = 0; chunkIndex < totalChunks / 2; chunkIndex++) {
                int bytesRead = fis.read(buffer); // 파일에서 데이터를 읽음

                // 청크 데이터를 MultipartFile로 감싸서 업로드
                MockMultipartFile chunk = new MockMultipartFile(
                        "file",
                        generatedUUID + ".chunk" + chunkIndex, // 생성된 UUID 사용
                        "video/mp4",
                        buffer
                        // 읽은 데이터 크기만큼 업로드
                );

                mockMvc.perform(multipart("/api/upload/chunk")
                                .file(chunk)
                                .param("fileName", generatedUUID) // 생성된 UUID 사용
                                .param("chunkIndex", String.valueOf(chunkIndex))
                                .param("totalChunks", String.valueOf(totalChunks)))
                        .andExpect(status().isOk());
            }

            // 파일 업로드 도중 멈추는 시뮬레이션 (중단)
            System.out.println("업로드 중단 후 재개...");

            // 나머지 청크 업로드 재개
            for (int chunkIndex = totalChunks / 2; chunkIndex < totalChunks; chunkIndex++) {
                int bytesRead = fis.read(buffer); // 파일에서 데이터를 읽음

                // 청크 데이터를 MultipartFile로 감싸서 업로드
                MockMultipartFile chunk = new MockMultipartFile(
                        "file",
                        generatedUUID + ".chunk" + chunkIndex, // 생성된 UUID 사용
                        "video/mp4",
                        buffer
                        // 읽은 데이터 크기만큼 업로드
                );

                mockMvc.perform(multipart("/api/upload/chunk")
                                .file(chunk)
                                .param("fileName", generatedUUID) // 생성된 UUID 사용
                                .param("chunkIndex", String.valueOf(chunkIndex))
                                .param("totalChunks", String.valueOf(totalChunks)))
                        .andExpect(status().isOk());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
