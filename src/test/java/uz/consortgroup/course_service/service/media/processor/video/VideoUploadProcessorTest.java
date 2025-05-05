package uz.consortgroup.course_service.service.media.processor.video;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.course_service.dto.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.dto.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.course_service.dto.request.video.VideoUploadRequestDto;
import uz.consortgroup.course_service.dto.response.resource.ResourceTranslationResponseDto;
import uz.consortgroup.course_service.dto.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.entity.VideoMetaData;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.entity.enumeration.ResourceType;
import uz.consortgroup.course_service.mapper.ResourceTranslationMapper;
import uz.consortgroup.course_service.service.media.video.metadate.VideoMetadataService;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.translation.ResourceTranslationService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VideoUploadProcessorTest {

    @Mock
    private ResourceService resourceService;

    @Mock
    private ResourceTranslationService translationService;

    @Mock
    private ResourceTranslationMapper translationMapper;

    @Mock
    private VideoMetadataService videoMetadataService;

    @Spy
    @InjectMocks
    private VideoUploadProcessor processor;

    @Test
    public void testProcessSingle_Success() {
        UUID lessonId = UUID.randomUUID();
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.VIDEO_MP4;
        long fileSize = 1024L;
        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setFileUrl(fileUrl);
        resource.setOrderPosition(1);
        VideoUploadRequestDto dto = mock(VideoUploadRequestDto.class);
        VideoMetaData meta = new VideoMetaData();
        meta.setDuration(120);
        meta.setResolution("720p");
        List<ResourceTranslation> translations = Collections.emptyList();
        VideoUploadResponseDto responseDto = VideoUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .durationSeconds(120)
                .resolution("720p")
                .orderPosition(resource.getOrderPosition())
                .translations(Collections.emptyList())
                .build();

        when(dto.getTranslations()).thenReturn(Collections.emptyList());
        when(dto.getOrderPosition()).thenReturn(1);
        when(dto.getDuration()).thenReturn(120);
        when(dto.getResolution()).thenReturn("720p");
        when(resourceService.create(lessonId, ResourceType.VIDEO, fileUrl, fileSize, mimeType, 1)).thenReturn(resource);
        when(videoMetadataService.create(resource.getId(), 120, "720p")).thenReturn(meta);
        when(translationService.findResourceTranslationById(resource.getId())).thenReturn(translations);

        VideoUploadResponseDto result = processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize);

        verify(translationService, never()).saveAllTranslations(anyList());
        verify(videoMetadataService).create(resource.getId(), 120, "720p");
        verify(translationService).findResourceTranslationById(resource.getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testProcessSingle_WithTranslations() {
        UUID lessonId = UUID.randomUUID();
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.VIDEO_MP4;
        long fileSize = 1024L;
        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setFileUrl(fileUrl);
        resource.setOrderPosition(1);
        VideoUploadRequestDto dto = mock(VideoUploadRequestDto.class);
        List<ResourceTranslationRequestDto> translationDtos = List.of(new ResourceTranslationRequestDto());
        VideoMetaData meta = new VideoMetaData();
        meta.setDuration(120);
        meta.setResolution("720p");
        List<ResourceTranslation> translations = List.of(new ResourceTranslation());
        ResourceTranslationResponseDto translationResponse = new ResourceTranslationResponseDto();
        VideoUploadResponseDto responseDto = VideoUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .durationSeconds(120)
                .resolution("720p")
                .orderPosition(resource.getOrderPosition())
                .translations(List.of(translationResponse))
                .build();

        when(dto.getTranslations()).thenReturn(translationDtos);
        when(dto.getOrderPosition()).thenReturn(1);
        when(dto.getDuration()).thenReturn(120);
        when(dto.getResolution()).thenReturn("720p");
        when(resourceService.create(lessonId, ResourceType.VIDEO, fileUrl, fileSize, mimeType, 1)).thenReturn(resource);
        when(videoMetadataService.create(resource.getId(), 120, "720p")).thenReturn(meta);
        when(translationService.findResourceTranslationById(resource.getId())).thenReturn(translations);
        when(translationMapper.toResponseDto(any(ResourceTranslation.class))).thenReturn(translationResponse);

        VideoUploadResponseDto result = processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize);

        verify(translationService).saveAllTranslations(anyList());
        verify(videoMetadataService).create(resource.getId(), 120, "720p");
        verify(translationService).findResourceTranslationById(resource.getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testProcessSingle_NullResource() {
        UUID lessonId = UUID.randomUUID();
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.VIDEO_MP4;
        long fileSize = 1024L;
        VideoUploadRequestDto dto = mock(VideoUploadRequestDto.class);

        when(dto.getOrderPosition()).thenReturn(1);
        when(resourceService.create(lessonId, ResourceType.VIDEO, fileUrl, fileSize, mimeType, 1)).thenReturn(null);
        doReturn(null).when(processor).buildSingleResponse(null, dto); // Замокаем buildSingleResponse для случая null ресурса

        VideoUploadResponseDto result = processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize);

        verify(translationService, never()).saveAllTranslations(anyList());
        verify(videoMetadataService, never()).create(any(), anyInt(), anyString());
        verify(translationService, never()).findResourceTranslationById(any());
        assertEquals(null, result);
    }

    @Test
    public void testProcessSingle_NoTranslations() {
        UUID lessonId = UUID.randomUUID();
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.VIDEO_MP4;
        long fileSize = 1024L;
        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setFileUrl(fileUrl);
        resource.setOrderPosition(1);
        VideoUploadRequestDto dto = mock(VideoUploadRequestDto.class);
        VideoMetaData meta = new VideoMetaData();
        meta.setDuration(120);
        meta.setResolution("720p");
        List<ResourceTranslation> translations = Collections.emptyList();
        VideoUploadResponseDto responseDto = VideoUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .durationSeconds(120)
                .resolution("720p")
                .orderPosition(resource.getOrderPosition())
                .translations(Collections.emptyList())
                .build();

        when(dto.getTranslations()).thenReturn(null);
        when(dto.getOrderPosition()).thenReturn(1);
        when(dto.getDuration()).thenReturn(120);
        when(dto.getResolution()).thenReturn("720p");
        when(resourceService.create(lessonId, ResourceType.VIDEO, fileUrl, fileSize, mimeType, 1)).thenReturn(resource);
        when(videoMetadataService.create(resource.getId(), 120, "720p")).thenReturn(meta);
        when(translationService.findResourceTranslationById(resource.getId())).thenReturn(translations);

        VideoUploadResponseDto result = processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize);

        verify(translationService, never()).saveAllTranslations(anyList());
        verify(videoMetadataService).create(resource.getId(), 120, "720p");
        verify(translationService).findResourceTranslationById(resource.getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testExtractDtos_ThrowsUnsupportedOperationException() {
        BulkVideoUploadRequestDto bulkDto = mock(BulkVideoUploadRequestDto.class);

        assertThrows(UnsupportedOperationException.class, () -> processor.extractDtos(bulkDto));
    }

    @Test
    public void testCreateResource_Success() {
        UUID lessonId = UUID.randomUUID();
        VideoUploadRequestDto dto = mock(VideoUploadRequestDto.class);
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.VIDEO_MP4;
        long fileSize = 1024L;
        Resource resource = new Resource();

        when(dto.getOrderPosition()).thenReturn(1);
        when(resourceService.create(lessonId, ResourceType.VIDEO, fileUrl, fileSize, mimeType, 1)).thenReturn(resource);

        Resource result = processor.createResource(lessonId, dto, fileUrl, mimeType, fileSize);

        assertNotNull(result);
        assertEquals(resource, result);
    }

    @Test
    public void testSaveTranslations_Success() {
        VideoUploadRequestDto dto = mock(VideoUploadRequestDto.class);
        Resource resource = new Resource();
        List<ResourceTranslationRequestDto> translations = List.of(new ResourceTranslationRequestDto());

        when(dto.getTranslations()).thenReturn(translations);

        processor.saveTranslations(dto, resource);

        verify(translationService).saveAllTranslations(anyList());
    }

    @Test
    public void testBuildSingleResponse_Success() {
        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setFileUrl("test-url");
        resource.setOrderPosition(1);
        VideoUploadRequestDto dto = mock(VideoUploadRequestDto.class);
        VideoMetaData meta = new VideoMetaData();
        meta.setDuration(120);
        meta.setResolution("720p");
        List<ResourceTranslation> translations = Collections.emptyList();
        VideoUploadResponseDto responseDto = VideoUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .durationSeconds(120)
                .resolution("720p")
                .orderPosition(resource.getOrderPosition())
                .translations(Collections.emptyList())
                .build();

        when(dto.getDuration()).thenReturn(120);
        when(dto.getResolution()).thenReturn("720p");
        when(videoMetadataService.create(resource.getId(), 120, "720p")).thenReturn(meta);
        when(translationService.findResourceTranslationById(resource.getId())).thenReturn(translations);

        VideoUploadResponseDto result = processor.buildSingleResponse(resource, dto);

        verify(videoMetadataService).create(resource.getId(), 120, "720p");
        verify(translationService).findResourceTranslationById(resource.getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testPrepareResources_ThrowsUnsupportedOperationException() {
        UUID lessonId = UUID.randomUUID();
        List<VideoUploadRequestDto> dtos = List.of(mock(VideoUploadRequestDto.class));
        List<String> fileUrls = List.of("url1");
        List<MimeType> mimeTypes = List.of(MimeType.VIDEO_MP4);
        List<Long> fileSizes = List.of(1024L);

        assertThrows(UnsupportedOperationException.class, () -> processor.prepareResources(lessonId, dtos, fileUrls, mimeTypes, fileSizes));
    }

    @Test
    public void testSaveAllTranslations_ThrowsUnsupportedOperationException() {
        List<VideoUploadRequestDto> dtos = List.of(mock(VideoUploadRequestDto.class));
        List<Resource> resources = List.of(new Resource());

        assertThrows(UnsupportedOperationException.class, () -> processor.saveAllTranslations(dtos, resources));
    }

    @Test
    public void testBuildBulkResponse_ThrowsUnsupportedOperationException() {
        List<Resource> resources = List.of(new Resource());
        List<VideoUploadRequestDto> dtos = List.of(mock(VideoUploadRequestDto.class));

        assertThrows(UnsupportedOperationException.class, () -> processor.buildBulkResponse(resources, dtos));
    }

    @Test
    public void testProcessBulkUpload_ThrowsUnsupportedOperationException() {
        UUID lessonId = UUID.randomUUID();
        BulkVideoUploadRequestDto bulkDto = mock(BulkVideoUploadRequestDto.class);
        List<String> fileUrls = List.of("url1");
        List<MimeType> mimeTypes = List.of(MimeType.VIDEO_MP4);
        List<Long> fileSizes = List.of(1024L);

        assertThrows(UnsupportedOperationException.class, () -> processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes));
    }
}