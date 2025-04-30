package uz.consortgroup.course_service.service.media.processor.image;

import org.springframework.stereotype.Component;
import uz.consortgroup.course_service.dto.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.course_service.dto.request.image.ImageUploadRequestDto;
import uz.consortgroup.course_service.dto.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.dto.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.course_service.dto.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.entity.enumeration.ResourceType;
import uz.consortgroup.course_service.mapper.ResourceTranslationMapper;
import uz.consortgroup.course_service.service.media.processor.AbstractMediaUploadProcessor;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.ResourceTranslationService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class BulkImageUploadProcessor extends AbstractMediaUploadProcessor<ImageUploadRequestDto, Void, BulkImageUploadResponseDto> {
    public BulkImageUploadProcessor(ResourceService resourceService, ResourceTranslationService translationService, ResourceTranslationMapper translationMapper) {
        super(resourceService, translationService, translationMapper);
    }

    @Override
    protected Resource createResource(UUID lessonId, ImageUploadRequestDto dto, String fileUrl, MimeType mimeType) {
        throw new UnsupportedOperationException("This method is not used in bulk upload processor");
    }

    @Override
    protected List<Resource> prepareResources(UUID lessonId, List<ImageUploadRequestDto> dtos, List<String> fileUrls) {
        List<Resource> resources = new ArrayList<>();
        for (int i = 0; i < dtos.size(); i++) {
            ImageUploadRequestDto img = dtos.get(i);
            String url = fileUrls.get(i);
            MimeType mimeType = MimeType.fromContentType(img.getImage().getContentType());
            resources.add(Resource.builder()
                    .lesson(Lesson.builder().id(lessonId).build())
                    .resourceType(ResourceType.IMAGE)
                    .fileUrl(url)
                    .fileSize(img.getImage().getSize())
                    .mimeType(mimeType)
                    .orderPosition(img.getOrderPosition())
                    .build());
        }
        return resources;
    }

    @Override
    protected void saveTranslations(ImageUploadRequestDto dto, Resource resource) {
        throw new UnsupportedOperationException("This method is not used in bulk upload processor");
    }

    @Override
    protected void saveAllTranslations(List<ImageUploadRequestDto> dtos, List<Resource> resources) {
        List<ResourceTranslation> translations = new ArrayList<>();
        for (int i = 0; i < dtos.size(); i++) {
            ImageUploadRequestDto img = dtos.get(i);
            Resource res = resources.get(i);
            if (img.getTranslations() != null) {
                for (ResourceTranslationRequestDto t : img.getTranslations()) {
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
    protected Void buildSingleResponse(Resource resource, ImageUploadRequestDto dto) {
        throw new UnsupportedOperationException("This method is not used in bulk upload processor");
    }

    @Override
    protected BulkImageUploadResponseDto buildBulkResponse(List<Resource> resources, List<ImageUploadRequestDto> dtos) {
        List<ImageUploadResponseDto> imageDtos = resources.stream()
                .map(res -> {
                    List<ResourceTranslation> translations = translationService.findResourceTranslationById(res.getId());
                    return ImageUploadResponseDto.builder()
                            .resourceId(res.getId())
                            .fileUrl(res.getFileUrl())
                            .orderPosition(res.getOrderPosition())
                            .translations(mapTranslations(translations))
                            .build();
                })
                .toList();
        return BulkImageUploadResponseDto.builder()
                .images(imageDtos)
                .build();
    }

    public BulkImageUploadResponseDto processBulkUpload(UUID lessonId, BulkImageUploadRequestDto dto, List<String> fileUrls) {
        return processBulk(lessonId, dto.getImages(), fileUrls);
    }
}