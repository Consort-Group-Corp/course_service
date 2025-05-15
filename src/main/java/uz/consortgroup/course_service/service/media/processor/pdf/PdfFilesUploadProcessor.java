package uz.consortgroup.course_service.service.media.processor.pdf;

import org.springframework.stereotype.Service;
import uz.consortgroup.core.api.v1.dto.enumeration.MimeType;
import uz.consortgroup.core.api.v1.dto.enumeration.ResourceType;
import uz.consortgroup.core.api.v1.dto.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.response.pdf.BulkPdfFilesUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.mapper.ResourceTranslationMapper;
import uz.consortgroup.course_service.service.media.processor.AbstractMediaUploadProcessor;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.translation.ResourceTranslationService;

import java.util.List;
import java.util.UUID;

@Service
public class PdfFilesUploadProcessor extends AbstractMediaUploadProcessor<PdfFileUploadRequestDto, PdfFileUploadResponseDto,
        BulkPdfFilesUploadRequestDto, BulkPdfFilesUploadResponseDto> {
    public PdfFilesUploadProcessor(ResourceService resourceService, ResourceTranslationService translationService, ResourceTranslationMapper translationMapper) {
        super(resourceService, translationService, translationMapper);
    }

    @Override
    protected List<PdfFileUploadRequestDto> extractDtos(BulkPdfFilesUploadRequestDto bulkDto) {
        return List.of();
    }

    @Override
    protected Resource createResource(UUID lessonId, PdfFileUploadRequestDto dto, String fileUrl, MimeType mimeType, long fileSize) {
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
    protected List<Resource> prepareResources(UUID lessonId, List<PdfFileUploadRequestDto> pdfFileUploadRequestDtos, List<String> fileUrls, List<MimeType> mimeTypes, List<Long> fileSizes) {
        throw new UnsupportedOperationException("This method is not used in single upload processor");
    }

    @Override
    protected void saveTranslations(PdfFileUploadRequestDto dto, Resource resource) {
        if (dto.getTranslations() != null && !dto.getTranslations().isEmpty()) {
            translationService.saveTranslations(dto.getTranslations(), resource);
        }
    }

    @Override
    protected void saveAllTranslations(List<PdfFileUploadRequestDto> pdfFileUploadRequestDtos, List<Resource> resources) {
        throw new UnsupportedOperationException("This method is not used in single upload processor");
    }

    @Override
    protected PdfFileUploadResponseDto buildSingleResponse(Resource resource, PdfFileUploadRequestDto pdfFileUploadRequestDto) {
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
        throw new UnsupportedOperationException("This method is not used in single upload processor");
    }
}
