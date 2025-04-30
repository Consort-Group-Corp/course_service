package uz.consortgroup.course_service.service.media.processor.video;

import org.springframework.stereotype.Component;
import uz.consortgroup.course_service.dto.request.video.VideoUploadRequestDto;
import uz.consortgroup.course_service.dto.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.entity.VideoMetaData;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.entity.enumeration.ResourceType;
import uz.consortgroup.course_service.mapper.ResourceTranslationMapper;
import uz.consortgroup.course_service.service.media.processor.AbstractMediaUploadProcessor;
import uz.consortgroup.course_service.service.media.video.VideoMetadataService;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.ResourceTranslationService;

import java.util.List;
import java.util.UUID;

@Component
public class VideoUploadProcessor extends AbstractMediaUploadProcessor<VideoUploadRequestDto, VideoUploadResponseDto, Void> {
    private final VideoMetadataService videoMetadataService;

    public VideoUploadProcessor(ResourceService resourceService, ResourceTranslationService translationService, ResourceTranslationMapper translationMapper, VideoMetadataService videoMetadataService) {
        super(resourceService, translationService, translationMapper);
        this.videoMetadataService = videoMetadataService;
    }

    @Override
    protected Resource createResource(UUID lessonId, VideoUploadRequestDto dto, String fileUrl, MimeType mimeType) {
        return resourceService.create(
                lessonId,
                ResourceType.VIDEO,
                fileUrl,
                dto.getVideo().getSize(),
                mimeType,
                dto.getOrderPosition()
        );
    }

    @Override
    protected List<Resource> prepareResources(UUID lessonId, List<VideoUploadRequestDto> dtos, List<String> fileUrls) {
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
    protected Void buildBulkResponse(List<Resource> resources, List<VideoUploadRequestDto> dtos) {
        throw new UnsupportedOperationException("This method is not used in single upload processor");
    }


    public VideoUploadResponseDto processSingleUpload(UUID lessonId, UUID courseId, VideoUploadRequestDto dto, String fileUrl, MimeType mimeType) {
        return processSingle(lessonId, dto, fileUrl, mimeType);
    }
}