//package com.boot.ksis.service.signage;
//
//import com.boot.ksis.dto.playlist.PlayListAddDTO;
//import com.boot.ksis.dto.playlist.PlayListDTO;
//import com.boot.ksis.dto.playlist.PlayListSequenceDTO;
//import com.boot.ksis.entity.Device;
//import com.boot.ksis.entity.EncodedResource;
//import com.boot.ksis.entity.MapsId.DeviceEncodeMap;
//import com.boot.ksis.entity.MapsId.PlaylistSequence;
//import com.boot.ksis.entity.PlayList;
//import com.boot.ksis.repository.playlist.PlayListRepository;
//import com.boot.ksis.repository.playlist.PlaylistSequenceRepository;
//import com.boot.ksis.repository.signage.DeviceEncodeMapRepository;
//import com.boot.ksis.repository.signage.SignageRepository;
//import com.boot.ksis.repository.upload.EncodedResourceRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//class SignageServiceTest {
//    @Mock
//    private SignageRepository signageRepository;
//
//    @Mock
//    private EncodedResourceRepository encodedResourceRepository;
//
//    @Mock
//    private DeviceEncodeMapRepository deviceEncodeMapRepository;
//
//    @Mock
//    private PlayListRepository playListRepository;
//
//    @Mock
//    private PlaylistSequenceRepository playlistSequenceRepository;
//
//    @InjectMocks
//    private SignageService signageService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testAddSignageResource() {
//        // Given
//        Long signageId = 1L;
//        List<Long> encodedResourceIdList = Arrays.asList(1L, 2L);
//
//        Device mockDevice = new Device();
//        mockDevice.setDeviceId(signageId);
//
//        EncodedResource mockEncodedResource1 = new EncodedResource();
//        mockEncodedResource1.setEncodedResourceId(1L);
//
//        EncodedResource mockEncodedResource2 = new EncodedResource();
//        mockEncodedResource2.setEncodedResourceId(2L);
//
//        // Mocking repository methods
//        when(signageRepository.findByDeviceId(signageId)).thenReturn(mockDevice);
//        when(encodedResourceRepository.findByEncodedResourceId(1L)).thenReturn(mockEncodedResource1);
//        when(encodedResourceRepository.findByEncodedResourceId(2L)).thenReturn(mockEncodedResource2);
//
//        Device fetchedDevice = signageRepository.findByDeviceId(signageId);
//        assertNotNull(fetchedDevice, "Expected device should not be null");
//
//        // When
//        signageService.addSignageResource(signageId, encodedResourceIdList);
//
//        // Then
//        verify(deviceEncodeMapRepository, times(2)).save(any(DeviceEncodeMap.class));
//    }
//
//    @Test
//    public void testAddPlaylist() {
//        // Given
//        PlayListAddDTO playListAddDTO = new PlayListAddDTO();
//        playListAddDTO.setDeviceId(1L);
//        playListAddDTO.setFileTitle("My Playlist");
//        playListAddDTO.setSlideTime(1);
//
//        // Mock Device
//        Device mockDevice = new Device();
//        mockDevice.setDeviceId(1L);
//
//        // Mock EncodedResource
//        EncodedResource mockEncodedResource = new EncodedResource();
//        mockEncodedResource.setEncodedResourceId(101L);
//
//        // Mock PlaylistSequenceDTO List
//        PlayListSequenceDTO sequenceDTO1 = new PlayListSequenceDTO();
//        sequenceDTO1.setEncodedResourceId(101L);
//        sequenceDTO1.setSequence(1);
//
//        List<PlayListSequenceDTO> playListSequenceDTOList = List.of(sequenceDTO1);
//
//        // Mocking repository methods
//        when(signageRepository.findByDeviceId(1L)).thenReturn(mockDevice);
//        when(encodedResourceRepository.findByEncodedResourceId(101L)).thenReturn(mockEncodedResource);
//
//        // When
//        signageService.addPlaylist(playListAddDTO, playListSequenceDTOList);
//
//        // Then
//        verify(playListRepository, times(1)).save(any(PlayList.class));
//        verify(playlistSequenceRepository, times(1)).save(any(PlaylistSequence.class));
//    }
//
//    @Test
//    public void testGetPlaylistList() {
//        Long signageId = 1L;
//
//        Device mockDevice = new Device();
//        mockDevice.setDeviceId(signageId);
//
//        List<PlayList> mockPlayLists = new ArrayList<>();
//
//        PlayList playList1 = new PlayList();
//        playList1.setPlaylistId(1L);
//        playList1.setFileTitle("My Playlist 1");
//        playList1.setRegTime(LocalDateTime.now());
//        playList1.setIsDefault(true);
//        playList1.setSlideTime(1);
//        playList1.setDevice(mockDevice);
//        playListRepository.save(playList1);
//
//        PlayList playList2 = new PlayList();
//        playList2.setPlaylistId(2L);
//        playList2.setFileTitle("My Playlist 2");
//        playList2.setRegTime(LocalDateTime.now());
//        playList2.setIsDefault(false);
//        playList2.setSlideTime(2);
//        playList2.setDevice(mockDevice);
//        playListRepository.save(playList2);
//
//        mockPlayLists.add(playList1);
//        mockPlayLists.add(playList2);
//
//        when(signageRepository.findByDeviceId(signageId)).thenReturn(mockDevice);
//        when(playListRepository.findByDevice(mockDevice)).thenReturn(mockPlayLists);
//
//        List<PlayListDTO> result = signageService.getPlaylistList(signageId);
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//
//        PlayListDTO dto1 = result.get(0);
//        assertEquals(1L, dto1.getPlaylistId());
//        assertEquals("My Playlist 1", dto1.getTitle());
//        assertFalse(dto1.getPlay());
//        assertEquals(1, dto1.getPlayTime());
//
//        // 두 번째 재생목록 검증
//        PlayListDTO dto2 = result.get(1);
//        assertEquals(2L, dto2.getPlaylistId());
//        assertEquals("My Playlist 2", dto2.getTitle());
//        assertTrue(dto2.getPlay());
//        assertEquals(2, dto2.getPlayTime());
//
//        // Verify the interactions
//        verify(signageRepository, times(1)).findByDeviceId(signageId);
//        verify(playListRepository, times(1)).findByDevice(mockDevice);
//    }
//}
