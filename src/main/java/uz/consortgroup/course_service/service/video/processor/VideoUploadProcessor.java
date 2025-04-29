package uz.consortgroup.course_service.service.video.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.consortgroup.course_service.dto.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.dto.request.video.VideoUploadRequestDto;
import uz.consortgroup.course_service.dto.response.resource.ResourceTranslationResponseDto;
import uz.consortgroup.course_service.dto.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.entity.VideoMetaData;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.entity.enumeration.ResourceType;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.ResourceTranslationService;
import uz.consortgroup.course_service.service.video.VideoMetadataService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoUploadProcessor {
    private final ResourceService resourceService;
    private final VideoMetadataService videoMetadataService;
    private final ResourceTranslationService resourceTranslationService;

    public VideoUploadResponseDto processSingleUpload(UUID lessonId,
            UUID courseId,
            VideoUploadRequestDto dto,
            String fileUrl,
            MimeType mimeType
    ) {
        Resource res = createResource(lessonId, dto, fileUrl, mimeType);
        VideoMetaData meta = createVideoMetadata(res, dto);
        List<ResourceTranslation> translations = processTranslations(dto, res);
        
        return buildResponse(res, meta, translations);
    }

    private Resource createResource(UUID lessonId, VideoUploadRequestDto dto, String fileUrl, MimeType mimeType) {
        return resourceService.create(
            lessonId,
            ResourceType.VIDEO,
            fileUrl,
            dto.getVideo().getSize(),
            mimeType,
            dto.getOrderPosition()
        );
    }

    private VideoMetaData createVideoMetadata(Resource resource, VideoUploadRequestDto dto) {
        return videoMetadataService.create(
            resource.getId(),
            dto.getDuration(),
            dto.getResolution()
        );
    }

    private List<ResourceTranslation> processTranslations(VideoUploadRequestDto dto, Resource resource) {
        if (dto.getTranslations() == null || dto.getTranslations().isEmpty()) {
            return List.of();
        }
        
        List<ResourceTranslation> translations = dto.getTranslations().stream()
            .map(t -> buildTranslation(t, resource))
            .toList();
            
        return resourceTranslationService.saveAllTranslations(translations);
    }

    private ResourceTranslation buildTranslation(ResourceTranslationRequestDto dto, Resource resource) {
        return ResourceTranslation.builder()
            .resource(resource)
            .language(dto.getLanguage())
            .title(dto.getTitle())
            .description(dto.getDescription())
            .build();
    }

    private VideoUploadResponseDto buildResponse(Resource resource, VideoMetaData metaData, List<ResourceTranslation> translations) {
        return VideoUploadResponseDto.builder()
            .resourceId(resource.getId())
            .fileUrl(resource.getFileUrl())
            .durationSeconds(metaData.getDuration())
            .resolution(metaData.getResolution())
            .orderPosition(resource.getOrderPosition())
            .translations(mapTranslationsToDto(translations))
            .build();
    }

    private List<ResourceTranslationResponseDto> mapTranslationsToDto(List<ResourceTranslation> translations) {
        return translations.stream()
            .map(this::mapTranslationToDto)
            .toList();
    }

    private ResourceTranslationResponseDto mapTranslationToDto(ResourceTranslation t) {
        return ResourceTranslationResponseDto.builder()
            .id(t.getId())
            .language(t.getLanguage())
            .title(t.getTitle())
            .description(t.getDescription())
            .build();
    }
}