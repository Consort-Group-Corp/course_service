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
import uz.consortgroup.course_service.dto.response.image.BulkImageUploadResponseDto;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BulkImageUploadProcessorTest {

    @Mock
    private ResourceService resourceService;

    @Mock
    private ResourceTranslationService translationService;

    @Mock
    private ResourceTranslationMapper translationMapper;

    @Spy
    @InjectMocks
    private BulkImageUploadProcessor processor;

    @Test
    public void testProcessBulkUpload_Success() {
        UUID lessonId = UUID.randomUUID();
        BulkImageUploadRequestDto bulkDto = mock(BulkImageUploadRequestDto.class);
        List<String> fileUrls = List.of("url1", "url2");
        List<MimeType> mimeTypes = List.of(MimeType.IMAGE_JPEG, MimeType.IMAGE_JPEG);
        List<Long> fileSizes = List.of(1024L, 2048L);
        List<ImageUploadRequestDto> dtos = List.of(mock(ImageUploadRequestDto.class), mock(ImageUploadRequestDto.class));
        List<Resource> resources = List.of(createResource("url1", 1), createResource("url2", 2));
        List<ImageUploadResponseDto> imageDtos = List.of(
                ImageUploadResponseDto.builder().resourceId(resources.get(0).getId()).fileUrl("url1").orderPosition(1).translations(Collections.emptyList()).build(),
                ImageUploadResponseDto.builder().resourceId(resources.get(1).getId()).fileUrl("url2").orderPosition(2).translations(Collections.emptyList()).build()
        );
        BulkImageUploadResponseDto responseDto = BulkImageUploadResponseDto.builder().images(imageDtos).build();

        when(bulkDto.getImages()).thenReturn(dtos);
        when(dtos.get(0).getOrderPosition()).thenReturn(1);
        when(dtos.get(0).getTranslations()).thenReturn(Collections.emptyList());
        when(dtos.get(1).getOrderPosition()).thenReturn(2);
        when(dtos.get(1).getTranslations()).thenReturn(Collections.emptyList());
        when(resourceService.saveAllResources(anyList())).thenReturn(resources);
        when(translationService.findResourceTranslationById(resources.get(0).getId())).thenReturn(Collections.emptyList());
        when(translationService.findResourceTranslationById(resources.get(1).getId())).thenReturn(Collections.emptyList());

        BulkImageUploadResponseDto result = processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes);

        verify(resourceService).saveAllResources(anyList());
        verify(translationService, times(1)).saveAllTranslations(anyList());
        verify(translationService).findResourceTranslationById(resources.get(0).getId());
        verify(translationService).findResourceTranslationById(resources.get(1).getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testProcessBulkUpload_WithTranslations() {
        UUID lessonId = UUID.randomUUID();
        BulkImageUploadRequestDto bulkDto = mock(BulkImageUploadRequestDto.class);
        List<String> fileUrls = List.of("url1", "url2");
        List<MimeType> mimeTypes = List.of(MimeType.IMAGE_JPEG, MimeType.IMAGE_JPEG);
        List<Long> fileSizes = List.of(1024L, 2048L);
        List<ImageUploadRequestDto> dtos = List.of(mock(ImageUploadRequestDto.class), mock(ImageUploadRequestDto.class));
        List<ResourceTranslationRequestDto> translationsDto = List.of(new ResourceTranslationRequestDto());
        List<Resource> resources = List.of(createResource("url1", 1), createResource("url2", 2));
        List<ResourceTranslation> translations = List.of(new ResourceTranslation());
        ResourceTranslationResponseDto translationResponse = new ResourceTranslationResponseDto();
        List<ImageUploadResponseDto> imageDtos = List.of(
                ImageUploadResponseDto.builder().resourceId(resources.get(0).getId()).fileUrl("url1").orderPosition(1).translations(List.of(translationResponse)).build(),
                ImageUploadResponseDto.builder().resourceId(resources.get(1).getId()).fileUrl("url2").orderPosition(2).translations(Collections.emptyList()).build()
        );
        BulkImageUploadResponseDto responseDto = BulkImageUploadResponseDto.builder().images(imageDtos).build();

        when(bulkDto.getImages()).thenReturn(dtos);
        when(dtos.get(0).getOrderPosition()).thenReturn(1);
        when(dtos.get(0).getTranslations()).thenReturn(translationsDto);
        when(dtos.get(1).getOrderPosition()).thenReturn(2);
        when(dtos.get(1).getTranslations()).thenReturn(null);
        when(resourceService.saveAllResources(anyList())).thenReturn(resources);
        when(translationService.findResourceTranslationById(resources.get(0).getId())).thenReturn(translations);
        when(translationService.findResourceTranslationById(resources.get(1).getId())).thenReturn(Collections.emptyList());
        when(translationMapper.toResponseDto(any(ResourceTranslation.class))).thenReturn(translationResponse);

        BulkImageUploadResponseDto result = processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes);

        verify(resourceService).saveAllResources(anyList());
        verify(translationService).saveAllTranslations(anyList());
        verify(translationService).findResourceTranslationById(resources.get(0).getId());
        verify(translationService).findResourceTranslationById(resources.get(1).getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testProcessBulkUpload_EmptyDtos() {
        UUID lessonId = UUID.randomUUID();
        BulkImageUploadRequestDto bulkDto = mock(BulkImageUploadRequestDto.class);
        List<String> fileUrls = List.of("url1");
        List<MimeType> mimeTypes = List.of(MimeType.IMAGE_JPEG);
        List<Long> fileSizes = List.of(1024L);
        List<ImageUploadRequestDto> dtos = Collections.emptyList();
        BulkImageUploadResponseDto responseDto = BulkImageUploadResponseDto.builder().images(Collections.emptyList()).build();

        when(bulkDto.getImages()).thenReturn(dtos);
        when(resourceService.saveAllResources(anyList())).thenReturn(Collections.emptyList());

        BulkImageUploadResponseDto result = processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes);

        verify(resourceService).saveAllResources(anyList());
        verify(translationService).saveAllTranslations(anyList());
        verify(translationService, never()).findResourceTranslationById(any());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testExtractDtos_Success() {
        BulkImageUploadRequestDto bulkDto = mock(BulkImageUploadRequestDto.class);
        List<ImageUploadRequestDto> dtos = List.of(mock(ImageUploadRequestDto.class));

        when(bulkDto.getImages()).thenReturn(dtos);

        List<ImageUploadRequestDto> result = processor.extractDtos(bulkDto);

        assertNotNull(result);
        assertEquals(dtos, result);
    }

    @Test
    public void testPrepareResources_Success() {
        UUID lessonId = UUID.randomUUID();
        List<ImageUploadRequestDto> dtos = List.of(mock(ImageUploadRequestDto.class));
        List<String> fileUrls = List.of("url1");
        List<MimeType> mimeTypes = List.of(MimeType.IMAGE_JPEG);
        List<Long> fileSizes = List.of(1024L);

        when(dtos.get(0).getOrderPosition()).thenReturn(1);

        List<Resource> result = processor.prepareResources(lessonId, dtos, fileUrls, mimeTypes, fileSizes);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("url1", result.get(0).getFileUrl());
        assertEquals(ResourceType.IMAGE, result.get(0).getResourceType());
        assertEquals(1024L, result.get(0).getFileSize());
        assertEquals(MimeType.IMAGE_JPEG, result.get(0).getMimeType());
        assertEquals(1, result.get(0).getOrderPosition());
    }

    @Test
    public void testSaveAllTranslations_Success() {
        List<ImageUploadRequestDto> dtos = List.of(mock(ImageUploadRequestDto.class));
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
        List<ImageUploadRequestDto> dtos = List.of(mock(ImageUploadRequestDto.class));
        List<ImageUploadResponseDto> imageDtos = List.of(
                ImageUploadResponseDto.builder().resourceId(resources.get(0).getId()).fileUrl("url1").orderPosition(1).translations(Collections.emptyList()).build()
        );
        BulkImageUploadResponseDto responseDto = BulkImageUploadResponseDto.builder().images(imageDtos).build();

        when(translationService.findResourceTranslationById(resources.get(0).getId())).thenReturn(Collections.emptyList());

        BulkImageUploadResponseDto result = processor.buildBulkResponse(resources, dtos);

        verify(translationService).findResourceTranslationById(resources.get(0).getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testCreateResource_ThrowsUnsupportedOperationException() {
        UUID lessonId = UUID.randomUUID();
        ImageUploadRequestDto dto = mock(ImageUploadRequestDto.class);
        String fileUrl = "url1";
        MimeType mimeType = MimeType.IMAGE_JPEG;
        long fileSize = 1024L;

        assertThrows(UnsupportedOperationException.class, () -> processor.createResource(lessonId, dto, fileUrl, mimeType, fileSize));
    }

    @Test
    public void testSaveTranslations_ThrowsUnsupportedOperationException() {
        ImageUploadRequestDto dto = mock(ImageUploadRequestDto.class);
        Resource resource = new Resource();

        assertThrows(UnsupportedOperationException.class, () -> processor.saveTranslations(dto, resource));
    }

    @Test
    public void testBuildSingleResponse_ThrowsUnsupportedOperationException() {
        Resource resource = new Resource();
        ImageUploadRequestDto dto = mock(ImageUploadRequestDto.class);

        assertThrows(UnsupportedOperationException.class, () -> processor.buildSingleResponse(resource, dto));
    }

    @Test
    public void testProcessSingle_ThrowsUnsupportedOperationException() {
        UUID lessonId = UUID.randomUUID();
        ImageUploadRequestDto dto = mock(ImageUploadRequestDto.class);
        String fileUrl = "url1";
        MimeType mimeType = MimeType.IMAGE_JPEG;
        long fileSize = 1024L;

        assertThrows(UnsupportedOperationException.class, () -> processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize));
    }

    @Test
    public void testExtractDtos_NullImages() {
        BulkImageUploadRequestDto bulkDto = mock(BulkImageUploadRequestDto.class);

        when(bulkDto.getImages()).thenReturn(null);

        List<ImageUploadRequestDto> result = processor.extractDtos(bulkDto);

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