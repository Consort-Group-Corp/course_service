package uz.consortgroup.course_service.service.media.processor.image;

import org.springframework.stereotype.Service;
import uz.consortgroup.core.api.v1.dto.enumeration.MimeType;
import uz.consortgroup.core.api.v1.dto.enumeration.ResourceType;
import uz.consortgroup.core.api.v1.dto.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.request.image.ImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.mapper.ResourceTranslationMapper;
import uz.consortgroup.course_service.service.media.processor.AbstractMediaUploadProcessor;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.translation.ResourceTranslationService;

import java.util.List;
import java.util.UUID;

@Service
public class ImageUploadProcessor extends AbstractMediaUploadProcessor<ImageUploadRequestDto, ImageUploadResponseDto,
        BulkImageUploadRequestDto, BulkImageUploadResponseDto> {

    public ImageUploadProcessor(ResourceService resourceService, ResourceTranslationService translationService, ResourceTranslationMapper translationMapper) {
        super(resourceService, translationService, translationMapper);
    }

    @Override
    protected List<ImageUploadRequestDto> extractDtos(BulkImageUploadRequestDto bulkDto) {
        throw new UnsupportedOperationException("This method is not used in single upload processor");
    }

    @Override
    protected Resource createResource(UUID lessonId, ImageUploadRequestDto dto, String fileUrl, MimeType mimeType, long fileSize) {
        return resourceService.create(
                lessonId,
                ResourceType.IMAGE,
                fileUrl,
                fileSize,
                mimeType,
                dto.getOrderPosition()
        );
    }

    @Override
    protected List<Resource> prepareResources(UUID lessonId, List<ImageUploadRequestDto> dtos, List<String> fileUrls, List<MimeType> mimeTypes, List<Long> fileSizes) {
        throw new UnsupportedOperationException("This method is not used in single upload processor");
    }

    @Override
    protected void saveTranslations(ImageUploadRequestDto dto, Resource resource) {
        if (dto.getTranslations() != null && !dto.getTranslations().isEmpty()) {
            translationService.saveTranslations(dto.getTranslations(), resource);
        }
    }

    @Override
    protected void saveAllTranslations(List<ImageUploadRequestDto> dtos, List<Resource> resources) {
        throw new UnsupportedOperationException("This method is not used in single upload processor");
    }

    @Override
    protected ImageUploadResponseDto buildSingleResponse(Resource resource, ImageUploadRequestDto dto) {
        List<ResourceTranslation> translations = translationService.findResourceTranslationById(resource.getId());
        return ImageUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .orderPosition(resource.getOrderPosition())
                .translations(mapTranslations(translations))
                .build();
    }

    @Override
    protected BulkImageUploadResponseDto buildBulkResponse(List<Resource> resources, List<ImageUploadRequestDto> dtos) {
        throw new UnsupportedOperationException("This method is not used in single upload processor");
    }
}