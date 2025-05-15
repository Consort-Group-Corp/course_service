package uz.consortgroup.course_service.service.media.processor.pdf;

import org.springframework.stereotype.Service;
import uz.consortgroup.core.api.v1.dto.course.enumeration.ResourceType;
import uz.consortgroup.core.api.v1.dto.course.enumeration.MimeType;
import uz.consortgroup.core.api.v1.dto.course.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.pdf.BulkPdfFilesUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.mapper.ResourceTranslationMapper;
import uz.consortgroup.course_service.service.media.processor.AbstractMediaUploadProcessor;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.translation.ResourceTranslationService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BulkPdfFilesUploadProcessor extends AbstractMediaUploadProcessor<PdfFileUploadRequestDto, PdfFileUploadResponseDto,
        BulkPdfFilesUploadRequestDto, BulkPdfFilesUploadResponseDto> {

    public BulkPdfFilesUploadProcessor(ResourceService resourceService, ResourceTranslationService translationService, ResourceTranslationMapper translationMapper) {
        super(resourceService, translationService, translationMapper);
    }

    @Override
    protected List<PdfFileUploadRequestDto> extractDtos(BulkPdfFilesUploadRequestDto bulkDto) {
       return bulkDto.getPdfs();
    }

    @Override
    protected Resource createResource(UUID lessonId, PdfFileUploadRequestDto dto, String fileUrl, MimeType mimeType, long fileSize) {
        throw new UnsupportedOperationException("This method is not used in bulk upload processor");
    }

    @Override
    protected List<Resource> prepareResources(UUID lessonId, List<PdfFileUploadRequestDto> dtos, List<String> fileUrls,
                                              List<MimeType> mimeTypes, List<Long> fileSizes) {
        List<Resource> resources = new ArrayList<>();
        for (int i = 0; i < dtos.size(); i++) {
            PdfFileUploadRequestDto pdf = dtos.get(i);
            String fileUrl = fileUrls.get(i);
            MimeType mimeType = mimeTypes.get(i);
            long fileSize = fileSizes.get(i);
            resources.add(Resource.builder()
                    .lesson(Lesson.builder().id(lessonId).build())
                    .resourceType(ResourceType.PDF)
                    .fileUrl(fileUrl)
                    .fileSize(fileSize)
                    .mimeType(mimeType)
                    .orderPosition(pdf.getOrderPosition())
                    .build());
        }
        return resources;
    }

    @Override
    protected void saveTranslations(PdfFileUploadRequestDto dto, Resource resource) {
        throw new UnsupportedOperationException("This method is not used in bulk upload processor");
    }

    @Override
    protected void saveAllTranslations(List<PdfFileUploadRequestDto> dtos, List<Resource> resources) {
        List<ResourceTranslation> translations = new ArrayList<>();
        for (int i = 0; i < dtos.size(); i++) {
            PdfFileUploadRequestDto pdf = dtos.get(i);
            Resource res = resources.get(i);
            if (pdf.getTranslations() != null) {
                for (ResourceTranslationRequestDto t : pdf.getTranslations()) {
                    translations.add(ResourceTranslation.builder()
                            .resource(res)
                            .language(t.getLanguage())
                            .title(t.getTitle())
                            .description(t.getDescription())
                            .build());
                }
            }
        }
        translationService.saveAllTranslations(translations);
    }

    @Override
    protected PdfFileUploadResponseDto buildSingleResponse(Resource resource, PdfFileUploadRequestDto dto) {
        throw new UnsupportedOperationException("This method is not used in bulk upload processor");
    }

    @Override
    protected BulkPdfFilesUploadResponseDto buildBulkResponse(List<Resource> resources, List<PdfFileUploadRequestDto> pdfFileUploadRequestDtos) {
        List<PdfFileUploadResponseDto> pdfFileDtos = resources.stream()
                .map(res -> {
                    List<ResourceTranslation> translations = translationService.findResourceTranslationById(res.getId());
                    return PdfFileUploadResponseDto.builder()
                            .resourceId(res.getId())
                            .fileUrl(res.getFileUrl())
                            .orderPosition(res.getOrderPosition())
                            .translations(mapTranslations(translations))
                            .build();
                })
                .toList();

        return BulkPdfFilesUploadResponseDto.builder()
                .pdfFiles(pdfFileDtos)
                .build();
    }
}
