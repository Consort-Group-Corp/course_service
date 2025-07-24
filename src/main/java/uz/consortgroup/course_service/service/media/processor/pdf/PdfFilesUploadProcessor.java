package uz.consortgroup.course_service.service.media.processor.pdf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.consortgroup.core.api.v1.dto.course.enumeration.MimeType;
import uz.consortgroup.core.api.v1.dto.course.enumeration.ResourceType;
import uz.consortgroup.core.api.v1.dto.course.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.pdf.BulkPdfFilesUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.mapper.ResourceTranslationMapper;
import uz.consortgroup.course_service.service.media.processor.AbstractMediaUploadProcessor;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.translation.ResourceTranslationService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PdfFilesUploadProcessor extends AbstractMediaUploadProcessor<PdfFileUploadRequestDto, PdfFileUploadResponseDto,
        BulkPdfFilesUploadRequestDto, BulkPdfFilesUploadResponseDto> {

    public PdfFilesUploadProcessor(ResourceService resourceService,
                                   ResourceTranslationService translationService,
                                   ResourceTranslationMapper translationMapper) {
        super(resourceService, translationService, translationMapper);
    }

    @Override
    protected List<PdfFileUploadRequestDto> extractDtos(BulkPdfFilesUploadRequestDto bulkDto) {
        log.debug("extractDtos() is not supported in PdfFilesUploadProcessor (single upload)");
        return List.of(); // method is not used in single upload
    }

    @Override
    protected Resource createResource(UUID lessonId, PdfFileUploadRequestDto dto, String fileUrl, MimeType mimeType, long fileSize) {
        log.debug("Creating PDF resource for lessonId={} with fileUrl={}", lessonId, fileUrl);
        return resourceService.create(
                lessonId,
                ResourceType.PDF,
                fileUrl,
                fileSize,
                mimeType,
                dto.getOrderPosition()
        );
    }

    @Override
    protected List<Resource> prepareResources(UUID lessonId,
                                              List<PdfFileUploadRequestDto> pdfFileUploadRequestDtos,
                                              List<String> fileUrls,
                                              List<MimeType> mimeTypes,
                                              List<Long> fileSizes) {
        throw new UnsupportedOperationException("prepareResources() is not used in single upload processor");
    }

    @Override
    protected void saveTranslations(PdfFileUploadRequestDto dto, Resource resource) {
        if (dto.getTranslations() != null && !dto.getTranslations().isEmpty()) {
            log.debug("Saving translations for PDF resource id={}", resource.getId());
            translationService.saveTranslations(dto.getTranslations(), resource);
        } else {
            log.debug("No translations to save for PDF resource id={}", resource.getId());
        }
    }

    @Override
    protected void saveAllTranslations(List<PdfFileUploadRequestDto> pdfFileUploadRequestDtos, List<Resource> resources) {
        throw new UnsupportedOperationException("saveAllTranslations() is not used in single upload processor");
    }

    @Override
    protected PdfFileUploadResponseDto buildSingleResponse(Resource resource, PdfFileUploadRequestDto pdfFileUploadRequestDto) {
        log.debug("Building single response for PDF resource id={}", resource.getId());
        List<ResourceTranslation> translations = translationService.findResourceTranslationById(resource.getId());

        return PdfFileUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .orderPosition(resource.getOrderPosition())
                .translations(mapTranslations(translations))
                .build();
    }

    @Override
    protected BulkPdfFilesUploadResponseDto buildBulkResponse(List<Resource> resources, List<PdfFileUploadRequestDto> pdfFileUploadRequestDtos) {
        throw new UnsupportedOperationException("buildBulkResponse() is not used in single upload processor");
    }
}
