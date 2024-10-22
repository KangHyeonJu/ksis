/*
package com.boot.ksis.controller.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.boot.ksis.service.file.FileBoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class FileBoardControllerTest {
    @InjectMocks
    private FileBoardController fileBoardController;

    @Mock
    private FileBoardService fileBoardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("파일 비활성화 테스트")
    void testDeactivationFile() {
        Long id = 1L;

        // Service 메서드 호출 확인
        doNothing().when(fileBoardService).deactivationFile(anyLong());

        ResponseEntity<Void> response = fileBoardController.deactivationFile(id);

        // 서비스가 호출되었는지 확인
        verify(fileBoardService, times(1)).deactivationFile(id);
        // 응답 상태가 204 No Content인지 확인
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("파일 삭제 테스트")
    void testDeleteEncodedFile() {
        Long id = 1L;

        // Service 메서드 호출 확인
        doNothing().when(fileBoardService).deleteEncodedFile(anyLong());

        ResponseEntity<Void> response = fileBoardController.deleteEncodedFile(id);

        // 서비스가 호출되었는지 확인
        verify(fileBoardService, times(1)).deleteEncodedFile(id);
        // 응답 상태가 204 No Content인지 확인
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

}
*/
