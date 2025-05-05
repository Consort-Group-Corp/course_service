package uz.consortgroup.course_service.service.media.processor.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.course_service.dto.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.course_service.dto.request.image.ImageUploadRequestDto;
import uz.consortgroup.course_service.dto.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.dto.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.dto.response.resource.ResourceTranslationResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.entity.enumeration.ResourceType;
import uz.consortgroup.course_service.mapper.ResourceTranslationMapper;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.translation.ResourceTranslationService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageUploadProcessorTest {

    @Mock
    private ResourceService resourceService;

    @Mock
    private ResourceTranslationService translationService;

    @Mock
    private ResourceTranslationMapper translationMapper;

    @Spy
    @InjectMocks
    private ImageUploadProcessor processor;

    @Test
    public void testProcessSingle_Success() {
        UUID lessonId = UUID.randomUUID();
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.IMAGE_JPEG;
        long fileSize = 1024L;
        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setFileUrl(fileUrl);
        resource.setOrderPosition(1);
        ImageUploadRequestDto dto = mock(ImageUploadRequestDto.class);
        List<ResourceTranslation> translations = Collections.emptyList();
        ImageUploadResponseDto responseDto = ImageUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .orderPosition(resource.getOrderPosition())
                .translations(Collections.emptyList())
                .build();

        when(dto.getTranslations()).thenReturn(Collections.emptyList());
        when(dto.getOrderPosition()).thenReturn(1); // Добавляем мок для orderPosition
        when(resourceService.create(lessonId, ResourceType.IMAGE, fileUrl, fileSize, mimeType, 1)).thenReturn(resource);
        when(translationService.findResourceTranslationById(resource.getId())).thenReturn(translations);

        ImageUploadResponseDto result = processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize);

        verify(translationService, never()).saveTranslations(anyList(), anyMap());
        verify(translationService).findResourceTranslationById(resource.getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testProcessSingle_WithTranslations() {
        UUID lessonId = UUID.randomUUID();
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.IMAGE_JPEG;
        long fileSize = 1024L;
        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setFileUrl(fileUrl);
        resource.setOrderPosition(1);
        ImageUploadRequestDto dto = mock(ImageUploadRequestDto.class);
        List<ResourceTranslationRequestDto> translationDtos = List.of(new ResourceTranslationRequestDto());
        List<ResourceTranslation> translations = List.of(new ResourceTranslation());
        ResourceTranslationResponseDto translationResponse = new ResourceTranslationResponseDto();
        ImageUploadResponseDto responseDto = ImageUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .orderPosition(resource.getOrderPosition())
                .translations(List.of(translationResponse))
                .build();

        when(dto.getTranslations()).thenReturn(translationDtos);
        when(dto.getOrderPosition()).thenReturn(1);
        when(resourceService.create(lessonId, ResourceType.IMAGE, fileUrl, fileSize, mimeType, 1)).thenReturn(resource);
        when(translationService.findResourceTranslationById(resource.getId())).thenReturn(translations);
        when(translationMapper.toResponseDto(any(ResourceTranslation.class))).thenReturn(translationResponse);

        ImageUploadResponseDto result = processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize);

        verify(translationService).saveTranslations(translationDtos, resource);
        verify(translationService).findResourceTranslationById(resource.getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testProcessSingle_NoTranslations() {
        UUID lessonId = UUID.randomUUID();
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.IMAGE_JPEG;
        long fileSize = 1024L;
        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setFileUrl(fileUrl);
        resource.setOrderPosition(1);
        ImageUploadRequestDto dto = mock(ImageUploadRequestDto.class);
        List<ResourceTranslation> translations = Collections.emptyList();
        ImageUploadResponseDto responseDto = ImageUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .orderPosition(resource.getOrderPosition())
                .translations(Collections.emptyList())
                .build();

        when(dto.getTranslations()).thenReturn(null);
        when(dto.getOrderPosition()).thenReturn(1);
        when(resourceService.create(lessonId, ResourceType.IMAGE, fileUrl, fileSize, mimeType, 1)).thenReturn(resource);
        when(translationService.findResourceTranslationById(resource.getId())).thenReturn(translations);

        ImageUploadResponseDto result = processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize);

        verify(translationService, never()).saveTranslations(anyList(), anyMap());
        verify(translationService).findResourceTranslationById(resource.getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testExtractDtos_ThrowsUnsupportedOperationException() {
        BulkImageUploadRequestDto bulkDto = mock(BulkImageUploadRequestDto.class);

        assertThrows(UnsupportedOperationException.class, () -> processor.extractDtos(bulkDto));
    }

    @Test
    public void testPrepareResources_ThrowsUnsupportedOperationException() {
        UUID lessonId = UUID.randomUUID();
        List<ImageUploadRequestDto> dtos = List.of(mock(ImageUploadRequestDto.class));
        List<String> fileUrls = List.of("url1");
        List<MimeType> mimeTypes = List.of(MimeType.IMAGE_JPEG);
        List<Long> fileSizes = List.of(1024L);

        assertThrows(UnsupportedOperationException.class, () -> processor.prepareResources(lessonId, dtos, fileUrls, mimeTypes, fileSizes));
    }

    @Test
    public void testSaveAllTranslations_ThrowsUnsupportedOperationException() {
        List<ImageUploadRequestDto> dtos = List.of(mock(ImageUploadRequestDto.class));
        List<Resource> resources = List.of(new Resource());

        assertThrows(UnsupportedOperationException.class, () -> processor.saveAllTranslations(dtos, resources));
    }

    @Test
    public void testBuildBulkResponse_ThrowsUnsupportedOperationException() {
        List<Resource> resources = List.of(new Resource());
        List<ImageUploadRequestDto> dtos = List.of(mock(ImageUploadRequestDto.class));

        assertThrows(UnsupportedOperationException.class, () -> processor.buildBulkResponse(resources, dtos));
    }

    @Test
    public void testProcessBulkUpload_ThrowsUnsupportedOperationException() {
        UUID lessonId = UUID.randomUUID();
        BulkImageUploadRequestDto bulkDto = mock(BulkImageUploadRequestDto.class);
        List<String> fileUrls = List.of("url1");
        List<MimeType> mimeTypes = List.of(MimeType.IMAGE_JPEG);
        List<Long> fileSizes = List.of(1024L);

        assertThrows(UnsupportedOperationException.class, () -> processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes));
    }
}