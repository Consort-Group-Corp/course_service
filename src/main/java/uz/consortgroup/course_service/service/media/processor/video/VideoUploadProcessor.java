package uz.consortgroup.course_service.service.media.processor.video;

import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.course.enumeration.MimeType;
import uz.consortgroup.core.api.v1.dto.course.enumeration.ResourceType;
import uz.consortgroup.core.api.v1.dto.course.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.video.VideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.entity.VideoMetaData;
import uz.consortgroup.course_service.mapper.ResourceTranslationMapper;
import uz.consortgroup.course_service.service.media.processor.AbstractMediaUploadProcessor;
import uz.consortgroup.course_service.service.media.video.metadate.VideoMetadataService;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.translation.ResourceTranslationService;

import java.util.List;
import java.util.UUID;

@Component
public class VideoUploadProcessor extends AbstractMediaUploadProcessor<VideoUploadRequestDto, VideoUploadResponseDto,
        BulkVideoUploadRequestDto, BulkVideoUploadResponseDto> {

    private final VideoMetadataService videoMetadataService;

    public VideoUploadProcessor(ResourceService resourceService, ResourceTranslationService translationService, ResourceTranslationMapper translationMapper, VideoMetadataService videoMetadataService) {
        super(resourceService, translationService, translationMapper);
        this.videoMetadataService = videoMetadataService;
    }

    @Override
    protected List<VideoUploadRequestDto> extractDtos(BulkVideoUploadRequestDto bulkDto) {
        throw new UnsupportedOperationException("This method is not used in single upload processor");
    }

    @Override
    protected Resource createResource(UUID lessonId, VideoUploadRequestDto dto, String fileUrl, MimeType mimeType, long fileSize) {
        return resourceService.create(
                lessonId,
                ResourceType.VIDEO,
                fileUrl,
                fileSize,
                mimeType,
                dto.getOrderPosition()
        );
    }

    @Override
    protected List<Resource> prepareResources(UUID lessonId, List<VideoUploadRequestDto> dtos, List<String> fileUrls, List<MimeType> mimeTypes, List<Long> fileSizes) {
        throw new UnsupportedOperationException("This method is not used in single upload processor");
    }

    @Override
    protected void saveTranslations(VideoUploadRequestDto dto, Resource resource) {
        if (dto.getTranslations() != null && !dto.getTranslations().isEmpty()) {
            List<ResourceTranslation> translations = dto.getTranslations().stream()
                    .map(t -> ResourceTranslation.builder()
                            .resource(resource)
                            .language(t.getLanguage())
                            .title(t.getTitle())
                            .description(t.getDescription())
                            .build())
                    .toList();
            translationService.saveAllTranslations(translations);
        }
    }

    @Override
    protected void saveAllTranslations(List<VideoUploadRequestDto> dtos, List<Resource> resources) {
        throw new UnsupportedOperationException("This method is not used in single upload processor");
    }

    @Override
    protected VideoUploadResponseDto buildSingleResponse(Resource resource, VideoUploadRequestDto dto) {
        VideoMetaData meta = videoMetadataService.create(resource.getId(), dto.getDuration(), dto.getResolution());
        List<ResourceTranslation> translations = translationService.findResourceTranslationById(resource.getId());
        return VideoUploadResponseDto.builder()
                .resourceId(resource.getId())
                .fileUrl(resource.getFileUrl())
                .durationSeconds(meta.getDuration())
                .resolution(meta.getResolution())
                .orderPosition(resource.getOrderPosition())
                .translations(mapTranslations(translations))
                .build();
    }

    @Override
    protected BulkVideoUploadResponseDto buildBulkResponse(List<Resource> resources, List<VideoUploadRequestDto> dtos) {
        throw new UnsupportedOperationException("This method is not used in single upload processor");
    }
}