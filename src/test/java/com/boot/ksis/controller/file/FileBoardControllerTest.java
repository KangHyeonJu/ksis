/*
package com.boot.ksis.controller.file;

import com.boot.ksis.entity.EncodedResource;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.repository.signage.ThumbNailRepository;
import com.boot.ksis.repository.upload.EncodedResourceRepository;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import com.boot.ksis.service.file.FileBoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class FileBoardControllerTest {
    @Mock
    private OriginalResourceRepository originalResourceRepository;

    @Mock
    private ThumbNailRepository thumbNailRepository;

    @Mock
    private EncodedResourceRepository encodedResourceRepository;

    @InjectMocks
    private FileBoardService fileBoardService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDeleteFile() {
        Long originalResourceId = 1L;

        // Mocking OriginalResource entity
        OriginalResource mockOriginalResource = mock(OriginalResource.class);

        // Mocking repository methods
        when(originalResourceRepository.findByOriginalResourceId(originalResourceId)).thenReturn(mockOriginalResource);

        // 실제 메서드 호출
        assertDoesNotThrow(() -> fileBoardService.deleteFile(originalResourceId));

        // verify that the delete methods were called
        verify(thumbNailRepository, times(1)).deleteByOriginalResource(mockOriginalResource);
        verify(originalResourceRepository, times(1)).deleteById(originalResourceId);
        verify(encodedResourceRepository, times(1)).deleteByOriginalResource(mockOriginalResource);
    }

    @Test
    public void testDeleteEncodedFile() {
        Long encodedResourceId = 1L;

        // Mocking EncodedResource entity
        EncodedResource mockEncodedResource = mock(EncodedResource.class);

        // Mocking repository methods
        when(encodedResourceRepository.findByEncodedResourceId(encodedResourceId)).thenReturn(mockEncodedResource);

        // 실제 메서드 호출
        assertDoesNotThrow(() -> fileBoardService.deleteEncodedFile(encodedResourceId));

        // verify that the delete methods were called
        verify(encodedResourceRepository, times(1)).deleteById(encodedResourceId);
    }
}*/
