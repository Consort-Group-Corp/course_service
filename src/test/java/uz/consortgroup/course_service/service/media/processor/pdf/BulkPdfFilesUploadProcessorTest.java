package uz.consortgroup.course_service.service.media.processor.pdf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.enumeration.MimeType;
import uz.consortgroup.core.api.v1.dto.enumeration.ResourceType;
import uz.consortgroup.core.api.v1.dto.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.core.api.v1.dto.response.pdf.BulkPdfFilesUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.response.resource.ResourceTranslationResponseDto;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BulkPdfFilesUploadProcessorTest {

    @Mock
    private ResourceService resourceService;

    @Mock
    private ResourceTranslationService translationService;

    @Mock
    private ResourceTranslationMapper translationMapper;

    @Spy
    @InjectMocks
    private BulkPdfFilesUploadProcessor processor;

    @Test
    public void testProcessBulkUpload_Success() {
        UUID lessonId = UUID.randomUUID();
        BulkPdfFilesUploadRequestDto bulkDto = mock(BulkPdfFilesUploadRequestDto.class);
        List<String> fileUrls = List.of("url1", "url2");
        List<MimeType> mimeTypes = List.of(MimeType.APPLICATION_PDF, MimeType.APPLICATION_PDF);
        List<Long> fileSizes = List.of(1024L, 2048L);
        List<PdfFileUploadRequestDto> dtos = List.of(mock(PdfFileUploadRequestDto.class), mock(PdfFileUploadRequestDto.class));
        List<Resource> resources = List.of(createResource("url1", 1), createResource("url2", 2));
        List<PdfFileUploadResponseDto> pdfDtos = List.of(
                PdfFileUploadResponseDto.builder().resourceId(resources.get(0).getId()).fileUrl("url1").orderPosition(1).translations(Collections.emptyList()).build(),
                PdfFileUploadResponseDto.builder().resourceId(resources.get(1).getId()).fileUrl("url2").orderPosition(2).translations(Collections.emptyList()).build()
        );
        BulkPdfFilesUploadResponseDto responseDto = BulkPdfFilesUploadResponseDto.builder().pdfFiles(pdfDtos).build();

        when(bulkDto.getPdfs()).thenReturn(dtos);
        when(dtos.get(0).getOrderPosition()).thenReturn(1);
        when(dtos.get(0).getTranslations()).thenReturn(Collections.emptyList());
        when(dtos.get(1).getOrderPosition()).thenReturn(2);
        when(dtos.get(1).getTranslations()).thenReturn(Collections.emptyList());
        when(resourceService.saveAllResources(anyList())).thenReturn(resources);
        when(translationService.findResourceTranslationById(resources.get(0).getId())).thenReturn(Collections.emptyList());
        when(translationService.findResourceTranslationById(resources.get(1).getId())).thenReturn(Collections.emptyList());

        BulkPdfFilesUploadResponseDto result = processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes);

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
        BulkPdfFilesUploadRequestDto bulkDto = mock(BulkPdfFilesUploadRequestDto.class);
        List<String> fileUrls = List.of("url1", "url2");
        List<MimeType> mimeTypes = List.of(MimeType.APPLICATION_PDF, MimeType.APPLICATION_PDF);
        List<Long> fileSizes = List.of(1024L, 2048L);
        List<PdfFileUploadRequestDto> dtos = List.of(mock(PdfFileUploadRequestDto.class), mock(PdfFileUploadRequestDto.class));
        List<ResourceTranslationRequestDto> translationsDto = List.of(new ResourceTranslationRequestDto());
        List<Resource> resources = List.of(createResource("url1", 1), createResource("url2", 2));
        List<ResourceTranslation> translations = List.of(new ResourceTranslation());
        ResourceTranslationResponseDto translationResponse = new ResourceTranslationResponseDto();
        List<PdfFileUploadResponseDto> pdfDtos = List.of(
                PdfFileUploadResponseDto.builder().resourceId(resources.get(0).getId()).fileUrl("url1").orderPosition(1).translations(List.of(translationResponse)).build(),
                PdfFileUploadResponseDto.builder().resourceId(resources.get(1).getId()).fileUrl("url2").orderPosition(2).translations(Collections.emptyList()).build()
        );
        BulkPdfFilesUploadResponseDto responseDto = BulkPdfFilesUploadResponseDto.builder().pdfFiles(pdfDtos).build();

        when(bulkDto.getPdfs()).thenReturn(dtos);
        when(dtos.get(0).getOrderPosition()).thenReturn(1);
        when(dtos.get(0).getTranslations()).thenReturn(translationsDto);
        when(dtos.get(1).getOrderPosition()).thenReturn(2);
        when(dtos.get(1).getTranslations()).thenReturn(null);
        when(resourceService.saveAllResources(anyList())).thenReturn(resources);
        when(translationService.findResourceTranslationById(resources.get(0).getId())).thenReturn(translations);
        when(translationService.findResourceTranslationById(resources.get(1).getId())).thenReturn(Collections.emptyList());
        when(translationMapper.toResponseDto(any(ResourceTranslation.class))).thenReturn(translationResponse);

        BulkPdfFilesUploadResponseDto result = processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes);

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
        BulkPdfFilesUploadRequestDto bulkDto = mock(BulkPdfFilesUploadRequestDto.class);
        List<String> fileUrls = List.of("url1");
        List<MimeType> mimeTypes = List.of(MimeType.APPLICATION_PDF);
        List<Long> fileSizes = List.of(1024L);
        List<PdfFileUploadRequestDto> dtos = Collections.emptyList();
        BulkPdfFilesUploadResponseDto responseDto = BulkPdfFilesUploadResponseDto.builder().pdfFiles(Collections.emptyList()).build();

        when(bulkDto.getPdfs()).thenReturn(dtos);
        when(resourceService.saveAllResources(anyList())).thenReturn(Collections.emptyList());

        BulkPdfFilesUploadResponseDto result = processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes);

        verify(resourceService).saveAllResources(anyList());
        verify(translationService).saveAllTranslations(anyList());
        verify(translationService, never()).findResourceTranslationById(any());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testExtractDtos_Success() {
        BulkPdfFilesUploadRequestDto bulkDto = mock(BulkPdfFilesUploadRequestDto.class);
        List<PdfFileUploadRequestDto> dtos = List.of(mock(PdfFileUploadRequestDto.class));

        when(bulkDto.getPdfs()).thenReturn(dtos);

        List<PdfFileUploadRequestDto> result = processor.extractDtos(bulkDto);

        assertNotNull(result);
        assertEquals(dtos, result);
    }

    @Test
    public void testPrepareResources_Success() {
        UUID lessonId = UUID.randomUUID();
        List<PdfFileUploadRequestDto> dtos = List.of(mock(PdfFileUploadRequestDto.class));
        List<String> fileUrls = List.of("url1");
        List<MimeType> mimeTypes = List.of(MimeType.APPLICATION_PDF);
        List<Long> fileSizes = List.of(1024L);

        when(dtos.get(0).getOrderPosition()).thenReturn(1);

        List<Resource> result = processor.prepareResources(lessonId, dtos, fileUrls, mimeTypes, fileSizes);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("url1", result.get(0).getFileUrl());
        assertEquals(ResourceType.PDF, result.get(0).getResourceType());
        assertEquals(1024L, result.get(0).getFileSize());
        assertEquals(MimeType.APPLICATION_PDF, result.get(0).getMimeType());
        assertEquals(1, result.get(0).getOrderPosition());
    }

    @Test
    public void testSaveAllTranslations_Success() {
        List<PdfFileUploadRequestDto> dtos = List.of(mock(PdfFileUploadRequestDto.class));
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
        List<PdfFileUploadRequestDto> dtos = List.of(mock(PdfFileUploadRequestDto.class));
        List<PdfFileUploadResponseDto> pdfDtos = List.of(
                PdfFileUploadResponseDto.builder().resourceId(resources.get(0).getId()).fileUrl("url1").orderPosition(1).translations(Collections.emptyList()).build()
        );
        BulkPdfFilesUploadResponseDto responseDto = BulkPdfFilesUploadResponseDto.builder().pdfFiles(pdfDtos).build();

        when(translationService.findResourceTranslationById(resources.get(0).getId())).thenReturn(Collections.emptyList());

        BulkPdfFilesUploadResponseDto result = processor.buildBulkResponse(resources, dtos);

        verify(translationService).findResourceTranslationById(resources.get(0).getId());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testCreateResource_ThrowsUnsupportedOperationException() {
        UUID lessonId = UUID.randomUUID();
        PdfFileUploadRequestDto dto = mock(PdfFileUploadRequestDto.class);
        String fileUrl = "url1";
        MimeType mimeType = MimeType.APPLICATION_PDF;
        long fileSize = 1024L;

        assertThrows(UnsupportedOperationException.class, () -> processor.createResource(lessonId, dto, fileUrl, mimeType, fileSize));
    }

    @Test
    public void testSaveTranslations_ThrowsUnsupportedOperationException() {
        PdfFileUploadRequestDto dto = mock(PdfFileUploadRequestDto.class);
        Resource resource = new Resource();

        assertThrows(UnsupportedOperationException.class, () -> processor.saveTranslations(dto, resource));
    }

    @Test
    public void testBuildSingleResponse_ThrowsUnsupportedOperationException() {
        Resource resource = new Resource();
        PdfFileUploadRequestDto dto = mock(PdfFileUploadRequestDto.class);

        assertThrows(UnsupportedOperationException.class, () -> processor.buildSingleResponse(resource, dto));
    }

    @Test
    public void testProcessSingle_ThrowsUnsupportedOperationException() {
        UUID lessonId = UUID.randomUUID();
        PdfFileUploadRequestDto dto = mock(PdfFileUploadRequestDto.class);
        String fileUrl = "url1";
        MimeType mimeType = MimeType.APPLICATION_PDF;
        long fileSize = 1024L;

        assertThrows(UnsupportedOperationException.class, () -> processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize));
    }

    @Test
    public void testExtractDtos_NullPdfs() {
        BulkPdfFilesUploadRequestDto bulkDto = mock(BulkPdfFilesUploadRequestDto.class);

        when(bulkDto.getPdfs()).thenReturn(null);

        List<PdfFileUploadRequestDto> result = processor.extractDtos(bulkDto);

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