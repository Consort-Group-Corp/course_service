package uz.consortgroup.course_service.service.media.processor.video;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.course.enumeration.MimeType;
import uz.consortgroup.core.api.v1.dto.course.enumeration.ResourceType;
import uz.consortgroup.core.api.v1.dto.course.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.video.VideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.resource.ResourceTranslationResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.entity.VideoMetaData;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BulkVideoUploadProcessorTest {

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
    private BulkVideoUploadProcessor processor;

    @Test
    public void testProcessBulkUpload_Success() {
        UUID lessonId = UUID.randomUUID();
        BulkVideoUploadRequestDto bulkDto = mock(BulkVideoUploadRequestDto.class);
        List<String> fileUrls = List.of("url1", "url2");
        List<MimeType> mimeTypes = List.of(MimeType.VIDEO_MP4, MimeType.VIDEO_MP4);
        List<Long> fileSizes = List.of(1024L, 2048L);
        List<VideoUploadRequestDto> dtos = List.of(mock(VideoUploadRequestDto.class), mock(VideoUploadRequestDto.class));
        List<Resource> resources = List.of(createResource("url1", 1), createResource("url2", 2));
        VideoMetaData meta1 = new VideoMetaData();
        meta1.setDuration(120);
        meta1.setResolution("720p");
        VideoMetaData meta2 = new VideoMetaData();
        meta2.setDuration(240);
        meta2.setResolution("1080p");
        List<VideoUploadResponseDto> videoDtos = List.of(
                VideoUploadResponseDto.builder().resourceId(resources.get(0).getId()).fileUrl("url1").durationSeconds(120).resolution("720p").orderPosition(1).translations(Collections.emptyList()).build(),
                VideoUploadResponseDto.builder().resourceId(resources.get(1).getId()).fileUrl("url2").durationSeconds(240).resolution("1080p").orderPosition(2).translations(Collections.emptyList()).build()
        );
        BulkVideoUploadResponseDto responseDto = BulkVideoUploadResponseDto.builder().videos(videoDtos).build();

        when(bulkDto.getVideos()).thenReturn(dtos);
        when(dtos.get(0).getOrderPosition()).thenReturn(1);
        when(dtos.get(0).getTranslations()).thenReturn(Collections.emptyList());
        when(dtos.get(0).getDuration()).thenReturn(120);
        when(dtos.get(0).getResolution()).thenReturn("720p");
        when(dtos.get(1).getOrderPosition()).thenReturn(2);
        when(dtos.get(1).getTranslations()).thenReturn(Collections.emptyList());
        when(dtos.get(1).getDuration()).thenReturn(240);
        when(dtos.get(1).getResolution()).thenReturn("1080p");
        when(resourceService.saveAllResources(anyList())).thenReturn(resources);
        when(videoMetadataService.create(resources.get(0).getId(), 120, "720p")).thenReturn(meta1);
        when(videoMetadataService.create(resources.get(1).getId(), 240, "1080p")).thenReturn(meta2);
        when(translationService.findResourceTranslationById(resources.get(0).getId())).thenReturn(Collections.emptyList());
        when(translationService.findResourceTranslationById(resources.get(1).getId())).thenReturn(Collections.emptyList());

        BulkVideoUploadResponseDto result = processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes);

        verify(resourceService).saveAllResources(anyList());
        verify(translationService, times(1)).saveAllTranslations(anyList());
        verify(videoMetadataService).create(resources.get(0).getId(), 120, "720p");
        verify(videoMetadataService).create(resources.get(1).getId(), 240, "1080p");
        verify(translationService).findResourceTranslationById(resources.get(0).getId());
        verify(translationService).findResourceTranslationById(resources.get(1).getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testProcessBulkUpload_WithTranslations() {
        UUID lessonId = UUID.randomUUID();
        BulkVideoUploadRequestDto bulkDto = mock(BulkVideoUploadRequestDto.class);
        List<String> fileUrls = List.of("url1", "url2");
        List<MimeType> mimeTypes = List.of(MimeType.VIDEO_MP4, MimeType.VIDEO_MP4);
        List<Long> fileSizes = List.of(1024L, 2048L);
        List<VideoUploadRequestDto> dtos = List.of(mock(VideoUploadRequestDto.class), mock(VideoUploadRequestDto.class));
        List<ResourceTranslationRequestDto> translationsDto = List.of(new ResourceTranslationRequestDto());
        List<Resource> resources = List.of(createResource("url1", 1), createResource("url2", 2));
        VideoMetaData meta1 = new VideoMetaData();
        meta1.setDuration(120);
        meta1.setResolution("720p");
        VideoMetaData meta2 = new VideoMetaData();
        meta2.setDuration(240);
        meta2.setResolution("1080p");
        List<ResourceTranslation> translations = List.of(new ResourceTranslation());
        ResourceTranslationResponseDto translationResponse = new ResourceTranslationResponseDto();
        List<VideoUploadResponseDto> videoDtos = List.of(
                VideoUploadResponseDto.builder().resourceId(resources.get(0).getId()).fileUrl("url1").durationSeconds(120).resolution("720p").orderPosition(1).translations(List.of(translationResponse)).build(),
                VideoUploadResponseDto.builder().resourceId(resources.get(1).getId()).fileUrl("url2").durationSeconds(240).resolution("1080p").orderPosition(2).translations(Collections.emptyList()).build()
        );
        BulkVideoUploadResponseDto responseDto = BulkVideoUploadResponseDto.builder().videos(videoDtos).build();

        when(bulkDto.getVideos()).thenReturn(dtos);
        when(dtos.get(0).getOrderPosition()).thenReturn(1);
        when(dtos.get(0).getTranslations()).thenReturn(translationsDto);
        when(dtos.get(0).getDuration()).thenReturn(120);
        when(dtos.get(0).getResolution()).thenReturn("720p");
        when(dtos.get(1).getOrderPosition()).thenReturn(2);
        when(dtos.get(1).getTranslations()).thenReturn(null);
        when(dtos.get(1).getDuration()).thenReturn(240);
        when(dtos.get(1).getResolution()).thenReturn("1080p");
        when(resourceService.saveAllResources(anyList())).thenReturn(resources);
        when(videoMetadataService.create(resources.get(0).getId(), 120, "720p")).thenReturn(meta1);
        when(videoMetadataService.create(resources.get(1).getId(), 240, "1080p")).thenReturn(meta2);
        when(translationService.findResourceTranslationById(resources.get(0).getId())).thenReturn(translations);
        when(translationService.findResourceTranslationById(resources.get(1).getId())).thenReturn(Collections.emptyList());
        when(translationMapper.toResponseDto(any(ResourceTranslation.class))).thenReturn(translationResponse);

        BulkVideoUploadResponseDto result = processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes);

        verify(resourceService).saveAllResources(anyList());
        verify(translationService).saveAllTranslations(anyList());
        verify(videoMetadataService).create(resources.get(0).getId(), 120, "720p");
        verify(videoMetadataService).create(resources.get(1).getId(), 240, "1080p");
        verify(translationService).findResourceTranslationById(resources.get(0).getId());
        verify(translationService).findResourceTranslationById(resources.get(1).getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testProcessBulkUpload_EmptyDtos() {
        UUID lessonId = UUID.randomUUID();
        BulkVideoUploadRequestDto bulkDto = mock(BulkVideoUploadRequestDto.class);
        List<String> fileUrls = List.of("url1");
        List<MimeType> mimeTypes = List.of(MimeType.VIDEO_MP4);
        List<Long> fileSizes = List.of(1024L);
        List<VideoUploadRequestDto> dtos = Collections.emptyList();
        BulkVideoUploadResponseDto responseDto = BulkVideoUploadResponseDto.builder().videos(Collections.emptyList()).build();

        when(bulkDto.getVideos()).thenReturn(dtos);
        when(resourceService.saveAllResources(anyList())).thenReturn(Collections.emptyList());

        BulkVideoUploadResponseDto result = processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes);

        verify(resourceService).saveAllResources(anyList());
        verify(translationService).saveAllTranslations(anyList());
        verify(videoMetadataService, never()).create(any(), anyInt(), anyString());
        verify(translationService, never()).findResourceTranslationById(any());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testExtractDtos_Success() {
        BulkVideoUploadRequestDto bulkDto = mock(BulkVideoUploadRequestDto.class);
        List<VideoUploadRequestDto> dtos = List.of(mock(VideoUploadRequestDto.class));

        when(bulkDto.getVideos()).thenReturn(dtos);

        List<VideoUploadRequestDto> result = processor.extractDtos(bulkDto);

        assertNotNull(result);
        assertEquals(dtos, result);
    }

    @Test
    public void testPrepareResources_Success() {
        UUID lessonId = UUID.randomUUID();
        List<VideoUploadRequestDto> dtos = List.of(mock(VideoUploadRequestDto.class));
        List<String> fileUrls = List.of("url1");
        List<MimeType> mimeTypes = List.of(MimeType.VIDEO_MP4);
        List<Long> fileSizes = List.of(1024L);

        when(dtos.get(0).getOrderPosition()).thenReturn(1);

        List<Resource> result = processor.prepareResources(lessonId, dtos, fileUrls, mimeTypes, fileSizes);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("url1", result.get(0).getFileUrl());
        assertEquals(ResourceType.VIDEO, result.get(0).getResourceType());
        assertEquals(1024L, result.get(0).getFileSize());
        assertEquals(MimeType.VIDEO_MP4, result.get(0).getMimeType());
        assertEquals(1, result.get(0).getOrderPosition());
    }

    @Test
    public void testSaveAllTranslations_Success() {
        List<VideoUploadRequestDto> dtos = List.of(mock(VideoUploadRequestDto.class));
        List<Resource> resources = List.of(new Resource());
        List<ResourceTranslationRequestDto> translationsDto = List.of(new ResourceTranslationRequestDto());
        List<ResourceTranslation> translations = List.of(new ResourceTranslation());

        when(dtos.get(0).getTranslations()).thenReturn(translationsDto);
        when(translationService.saveAllTranslations(anyList())).thenReturn(translations);

        processor.saveAllTranslations(dtos, resources);

        verify(translationService).saveAllTranslations(anyList());
    }

    @Test
    public void testBuildBulkResponse_Success() {
        List<Resource> resources = List.of(createResource("url1", 1));
        List<VideoUploadRequestDto> dtos = List.of(mock(VideoUploadRequestDto.class));
        VideoMetaData meta = new VideoMetaData();
        meta.setDuration(120);
        meta.setResolution("720p");
        List<VideoUploadResponseDto> videoDtos = List.of(
                VideoUploadResponseDto.builder().resourceId(resources.get(0).getId()).fileUrl("url1").durationSeconds(120).resolution("720p").orderPosition(1).translations(Collections.emptyList()).build()
        );
        BulkVideoUploadResponseDto responseDto = BulkVideoUploadResponseDto.builder().videos(videoDtos).build();

        when(dtos.get(0).getDuration()).thenReturn(120);
        when(dtos.get(0).getResolution()).thenReturn("720p");
        when(videoMetadataService.create(resources.get(0).getId(), 120, "720p")).thenReturn(meta);
        when(translationService.findResourceTranslationById(resources.get(0).getId())).thenReturn(Collections.emptyList());

        BulkVideoUploadResponseDto result = processor.buildBulkResponse(resources, dtos);

        verify(videoMetadataService).create(resources.get(0).getId(), 120, "720p");
        verify(translationService).findResourceTranslationById(resources.get(0).getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testCreateResource_ThrowsUnsupportedOperationException() {
        UUID lessonId = UUID.randomUUID();
        VideoUploadRequestDto dto = mock(VideoUploadRequestDto.class);
        String fileUrl = "url1";
        MimeType mimeType = MimeType.VIDEO_MP4;
        long fileSize = 1024L;

        assertThrows(UnsupportedOperationException.class, () -> processor.createResource(lessonId, dto, fileUrl, mimeType, fileSize));
    }

    @Test
    public void testSaveTranslations_ThrowsUnsupportedOperationException() {
        VideoUploadRequestDto dto = mock(VideoUploadRequestDto.class);
        Resource resource = new Resource();

        assertThrows(UnsupportedOperationException.class, () -> processor.saveTranslations(dto, resource));
    }

    @Test
    public void testBuildSingleResponse_ThrowsUnsupportedOperationException() {
        Resource resource = new Resource();
        VideoUploadRequestDto dto = mock(VideoUploadRequestDto.class);

        assertThrows(UnsupportedOperationException.class, () -> processor.buildSingleResponse(resource, dto));
    }

    @Test
    public void testProcessSingle_ThrowsUnsupportedOperationException() {
        UUID lessonId = UUID.randomUUID();
        VideoUploadRequestDto dto = mock(VideoUploadRequestDto.class);
        String fileUrl = "url1";
        MimeType mimeType = MimeType.VIDEO_MP4;
        long fileSize = 1024L;

        assertThrows(UnsupportedOperationException.class, () -> processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize));
    }

    @Test
    public void testExtractDtos_NullVideos() {
        BulkVideoUploadRequestDto bulkDto = mock(BulkVideoUploadRequestDto.class);

        when(bulkDto.getVideos()).thenReturn(null);

        List<VideoUploadRequestDto> result = processor.extractDtos(bulkDto);

        assertEquals(null, result);
    }

    private Resource createResource(String fileUrl, int orderPosition) {
        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setFileUrl(fileUrl);
        resource.setOrderPosition(orderPosition);
        return resource;
    }
}