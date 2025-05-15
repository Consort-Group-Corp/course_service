package uz.consortgroup.course_service.service.media.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.enumeration.MimeType;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AbstractMediaUploadProcessorTest {

    @Mock
    private ResourceService resourceService;

    @Mock
    private ResourceTranslationService translationService;

    @Mock
    private ResourceTranslationMapper translationMapper;

    @Spy
    @InjectMocks
    private TestAbstractMediaUploadProcessor processor;

    @Test
    public void testProcessSingle_Success() {
        UUID lessonId = UUID.randomUUID();
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.APPLICATION_PDF;
        long fileSize = 1024L;
        Resource resource = new Resource();
        SingleRequestDto dto = mock(SingleRequestDto.class);
        SingleResponseDto responseDto = mock(SingleResponseDto.class);

        doReturn(resource).when(processor).createResource(lessonId, dto, fileUrl, mimeType, fileSize);
        doReturn(responseDto).when(processor).buildSingleResponse(resource, dto);

        SingleResponseDto result = processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize);

        verify(processor).saveTranslations(dto, resource);
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testProcessBulkUpload_Success() {
        UUID lessonId = UUID.randomUUID();
        BulkRequestDto bulkDto = mock(BulkRequestDto.class);
        List<String> fileUrls = List.of("url1", "url2");
        List<MimeType> mimeTypes = List.of(MimeType.APPLICATION_PDF, MimeType.APPLICATION_PDF);
        List<Long> fileSizes = List.of(1024L, 2048L);
        List<SingleRequestDto> dtos = List.of(mock(SingleRequestDto.class), mock(SingleRequestDto.class));
        List<Resource> resources = List.of(new Resource(), new Resource());
        BulkResponseDto responseDto = mock(BulkResponseDto.class);

        doReturn(dtos).when(processor).extractDtos(bulkDto);
        doReturn(resources).when(processor).prepareResources(lessonId, dtos, fileUrls, mimeTypes, fileSizes);
        when(resourceService.saveAllResources(resources)).thenReturn(resources);
        doReturn(responseDto).when(processor).buildBulkResponse(resources, dtos);

        BulkResponseDto result = processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes);

        verify(processor).saveAllTranslations(dtos, resources);
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testMapTranslations_EmptyList() {
        List<ResourceTranslationResponseDto> result = processor.mapTranslations(Collections.emptyList());

        assertEquals(0, result.size());
    }

    @Test
    public void testMapTranslations_NullList() {
        List<ResourceTranslationResponseDto> result = processor.mapTranslations(null);

        assertEquals(0, result.size());
    }

    @Test
    public void testProcessSingle_NullResource() {
        UUID lessonId = UUID.randomUUID();
        String fileUrl = "test-url";
        MimeType mimeType = MimeType.APPLICATION_PDF;
        long fileSize = 1024L;
        SingleRequestDto dto = mock(SingleRequestDto.class);

        doReturn(null).when(processor).createResource(lessonId, dto, fileUrl, mimeType, fileSize);

        SingleResponseDto result = processor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize);

        verify(processor, times(1)).saveTranslations(any(), any());
        assertEquals(null, result);
    }

    @Test
    public void testProcessBulkUpload_EmptyDtos() {
        UUID lessonId = UUID.randomUUID();
        BulkRequestDto bulkDto = mock(BulkRequestDto.class);
        List<String> fileUrls = List.of("url1");
        List<MimeType> mimeTypes = List.of(MimeType.APPLICATION_PDF);
        List<Long> fileSizes = List.of(1024L);
        List<SingleRequestDto> dtos = Collections.emptyList();
        BulkResponseDto responseDto = mock(BulkResponseDto.class);

        doReturn(dtos).when(processor).extractDtos(bulkDto);
        doReturn(Collections.emptyList()).when(processor).prepareResources(lessonId, dtos, fileUrls, mimeTypes, fileSizes);
        when(resourceService.saveAllResources(anyList())).thenReturn(Collections.emptyList());
        doReturn(responseDto).when(processor).buildBulkResponse(anyList(), anyList());

        BulkResponseDto result = processor.processBulkUpload(lessonId, bulkDto, fileUrls, mimeTypes, fileSizes);

        verify(processor, times(1)).saveAllTranslations(anyList(), anyList());
        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    public void testMapTranslations_WithTranslations() {
        ResourceTranslation translation = new ResourceTranslation();
        List<ResourceTranslation> translations = List.of(translation);
        ResourceTranslationResponseDto responseDto = new ResourceTranslationResponseDto();
        when(translationMapper.toResponseDto(translation)).thenReturn(responseDto);

        List<ResourceTranslationResponseDto> result = processor.mapTranslations(translations);

        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));
    }
}

interface SingleRequestDto {}
interface SingleResponseDto {}
interface BulkRequestDto {}
interface BulkResponseDto {}

class TestAbstractMediaUploadProcessor extends AbstractMediaUploadProcessor<SingleRequestDto, SingleResponseDto, BulkRequestDto, BulkResponseDto> {
    public TestAbstractMediaUploadProcessor(ResourceService resourceService, ResourceTranslationService translationService, ResourceTranslationMapper translationMapper) {
        super(resourceService, translationService, translationMapper);
    }

    @Override
    protected List<SingleRequestDto> extractDtos(BulkRequestDto bulkDto) { return null; }

    @Override
    protected Resource createResource(UUID lessonId, SingleRequestDto dto, String fileUrl, MimeType mimeType, long fileSize) { return null; }

    @Override
    protected List<Resource> prepareResources(UUID lessonId, List<SingleRequestDto> dtos, List<String> fileUrls, List<MimeType> mimeTypes, List<Long> fileSizes) { return null; }

    @Override
    protected void saveTranslations(SingleRequestDto dto, Resource resource) {}

    @Override
    protected void saveAllTranslations(List<SingleRequestDto> dtos, List<Resource> resources) {}

    @Override
    protected SingleResponseDto buildSingleResponse(Resource resource, SingleRequestDto dto) { return null; }

    @Override
    protected BulkResponseDto buildBulkResponse(List<Resource> resources, List<SingleRequestDto> dtos) { return null; }
}