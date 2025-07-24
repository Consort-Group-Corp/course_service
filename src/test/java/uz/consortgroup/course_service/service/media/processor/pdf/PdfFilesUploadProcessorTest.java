package uz.consortgroup.course_service.service.media.processor.pdf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.course.enumeration.MimeType;
import uz.consortgroup.core.api.v1.dto.course.enumeration.ResourceType;
import uz.consortgroup.core.api.v1.dto.course.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.resource.ResourceTranslationResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PdfFilesUploadProcessorTest {

    @Mock
    private ResourceService resourceService;

    @Mock
    private ResourceTranslationService translationService;

    @Mock
    private ResourceTranslationMapper translationMapper;

    @Spy
    @InjectMocks
    private PdfFilesUploadProcessor processor;

    @Test
    public void testProcessSingle_Success() {
        UUID lessonId = UUID.randomUUID();
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.APPLICATION_PDF;
        long fileSize = 1024L;
        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setFileUrl(fileUrl);
        resource.setOrderPosition(1);
        PdfFileUploadRequestDto dto = mock(PdfFileUploadRequestDto.class);
        List<ResourceTranslation> translations = Collections.emptyList();
        PdfFileUploadResponseDto responseDto = PdfFileUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .orderPosition(resource.getOrderPosition())
                .translations(Collections.emptyList())
                .build();

        when(dto.getTranslations()).thenReturn(Collections.emptyList());
        when(dto.getOrderPosition()).thenReturn(1);
        when(resourceService.create(lessonId, ResourceType.PDF, fileUrl, fileSize, mimeType, 1)).thenReturn(resource);
        when(translationService.findResourceTranslationById(resource.getId())).thenReturn(translations);

        PdfFileUploadResponseDto result = processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize);

        verify(translationService, never()).saveTranslations(anyList(), anyMap());
        verify(translationService).findResourceTranslationById(resource.getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testProcessSingle_WithTranslations() {
        UUID lessonId = UUID.randomUUID();
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.APPLICATION_PDF;
        long fileSize = 1024L;
        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setFileUrl(fileUrl);
        resource.setOrderPosition(1);
        PdfFileUploadRequestDto dto = mock(PdfFileUploadRequestDto.class);
        List<ResourceTranslationRequestDto> translationDtos = List.of(new ResourceTranslationRequestDto());
        List<ResourceTranslation> translations = List.of(new ResourceTranslation());
        ResourceTranslationResponseDto translationResponse = new ResourceTranslationResponseDto();
        PdfFileUploadResponseDto responseDto = PdfFileUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .orderPosition(resource.getOrderPosition())
                .translations(List.of(translationResponse))
                .build();

        when(dto.getTranslations()).thenReturn(translationDtos);
        when(dto.getOrderPosition()).thenReturn(1);
        when(resourceService.create(lessonId, ResourceType.PDF, fileUrl, fileSize, mimeType, 1)).thenReturn(resource);
        when(translationService.findResourceTranslationById(resource.getId())).thenReturn(translations);
        when(translationMapper.toResponseDto(any(ResourceTranslation.class))).thenReturn(translationResponse);

        PdfFileUploadResponseDto result = processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize);

        verify(translationService).saveTranslations(translationDtos, resource);
        verify(translationService).findResourceTranslationById(resource.getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testProcessSingle_NoTranslations() {
        UUID lessonId = UUID.randomUUID();
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.APPLICATION_PDF;
        long fileSize = 1024L;
        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setFileUrl(fileUrl);
        resource.setOrderPosition(1);
        PdfFileUploadRequestDto dto = mock(PdfFileUploadRequestDto.class);
        List<ResourceTranslation> translations = Collections.emptyList();
        PdfFileUploadResponseDto responseDto = PdfFileUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .orderPosition(resource.getOrderPosition())
                .translations(Collections.emptyList())
                .build();

        when(dto.getTranslations()).thenReturn(null);
        when(dto.getOrderPosition()).thenReturn(1);
        when(resourceService.create(lessonId, ResourceType.PDF, fileUrl, fileSize, mimeType, 1)).thenReturn(resource);
        when(translationService.findResourceTranslationById(resource.getId())).thenReturn(translations);

        PdfFileUploadResponseDto result = processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize);

        verify(translationService, never()).saveTranslations(anyList(), anyMap());
        verify(translationService).findResourceTranslationById(resource.getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testExtractDtos_EmptyList() {
        BulkPdfFilesUploadRequestDto bulkDto = mock(BulkPdfFilesUploadRequestDto.class);

        List<PdfFileUploadRequestDto> result = processor.extractDtos(bulkDto);

        assertNotNull(result);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void testCreateResource_Success() {
        UUID lessonId = UUID.randomUUID();
        PdfFileUploadRequestDto dto = mock(PdfFileUploadRequestDto.class);
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.APPLICATION_PDF;
        long fileSize = 1024L;
        Resource resource = new Resource();

        when(dto.getOrderPosition()).thenReturn(1);
        when(resourceService.create(lessonId, ResourceType.PDF, fileUrl, fileSize, mimeType, 1)).thenReturn(resource);

        Resource result = processor.createResource(lessonId, dto, fileUrl, mimeType, fileSize);

        assertNotNull(result);
        assertEquals(resource, result);
    }

    @Test
    public void testSaveTranslations_Success() {
        PdfFileUploadRequestDto dto = mock(PdfFileUploadRequestDto.class);
        Resource resource = new Resource();
        List<ResourceTranslationRequestDto> translations = List.of(new ResourceTranslationRequestDto());

        when(dto.getTranslations()).thenReturn(translations);

        processor.saveTranslations(dto, resource);

        verify(translationService).saveTranslations(translations, resource);
    }

    @Test
    public void testBuildSingleResponse_Success() {
        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setFileUrl("test-url");
        resource.setOrderPosition(1);
        PdfFileUploadRequestDto dto = mock(PdfFileUploadRequestDto.class);
        List<ResourceTranslation> translations = Collections.emptyList();
        PdfFileUploadResponseDto responseDto = PdfFileUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .orderPosition(resource.getOrderPosition())
                .translations(Collections.emptyList())
                .build();

        when(translationService.findResourceTranslationById(resource.getId())).thenReturn(translations);

        PdfFileUploadResponseDto result = processor.buildSingleResponse(resource, dto);

        verify(translationService).findResourceTranslationById(resource.getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testPrepareResources_ThrowsUnsupportedOperationException() {
        UUID lessonId = UUID.randomUUID();
        List<PdfFileUploadRequestDto> dtos = List.of(mock(PdfFileUploadRequestDto.class));
        List<String> fileUrls = List.of("url1");
        List<MimeType> mimeTypes = List.of(MimeType.APPLICATION_PDF);
        List<Long> fileSizes = List.of(1024L);

        assertThrows(UnsupportedOperationException.class, () -> processor.prepareResources(lessonId, dtos, fileUrls, mimeTypes, fileSizes));
    }

    @Test
    public void testSaveAllTranslations_ThrowsUnsupportedOperationException() {
        List<PdfFileUploadRequestDto> dtos = List.of(mock(PdfFileUploadRequestDto.class));
        List<Resource> resources = List.of(new Resource());

        assertThrows(UnsupportedOperationException.class, () -> processor.saveAllTranslations(dtos, resources));
    }

    @Test
    public void testBuildBulkResponse_ThrowsUnsupportedOperationException() {
        List<Resource> resources = List.of(new Resource());
        List<PdfFileUploadRequestDto> dtos = List.of(mock(PdfFileUploadRequestDto.class));

        assertThrows(UnsupportedOperationException.class, () -> processor.buildBulkResponse(resources, dtos));
    }

    @Test
    public void testProcessBulkUpload_ThrowsUnsupportedOperationException() {
        UUID lessonId = UUID.randomUUID();
        BulkPdfFilesUploadRequestDto bulkDto = mock(BulkPdfFilesUploadRequestDto.class);
        List<String> fileUrls = List.of("url1");
        List<MimeType> mimeTypes = List.of(MimeType.APPLICATION_PDF);
        List<Long> fileSizes = List.of(1024L);

        assertThrows(UnsupportedOperationException.class, () -> processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes));
    }
}