package uz.consortgroup.course_service.service.video.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.consortgroup.course_service.dto.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.dto.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.course_service.dto.request.video.VideoUploadRequestDto;
import uz.consortgroup.course_service.dto.response.resource.ResourceTranslationResponseDto;
import uz.consortgroup.course_service.dto.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.course_service.dto.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.entity.VideoMetaData;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.entity.enumeration.ResourceType;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.ResourceTranslationService;
import uz.consortgroup.course_service.service.video.VideoMetadataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BulkVideoUploadProcessor {
    private final ResourceService resourceService;
    private final VideoMetadataService videoMetadataService;
    private final ResourceTranslationService resourceTranslationService;

    public BulkVideoUploadResponseDto processBulkUpload(UUID lessonId, BulkVideoUploadRequestDto dto, List<String> fileUrls) {
        List<Resource> resources = prepareResources(lessonId, dto, fileUrls);
        List<VideoMetaData> metas = prepareMetas(dto, resources);
        
        resources = resourceService.saveAllResources(resources);
        videoMetadataService.saveAll(metas);
        
        List<ResourceTranslation> translations = processAllTranslations(dto, resources);
        associateTranslationsWithResources(resources, translations);
        
        return buildBulkResponse(resources, metas);
    }

    private List<Resource> prepareResources(UUID lessonId, BulkVideoUploadRequestDto dto, List<String> fileUrls) {
        List<Resource> resources = new ArrayList<>();
        
        for (int i = 0; i < dto.getVideos().size(); i++) {
            VideoUploadRequestDto vid = dto.getVideos().get(i);
            String url = fileUrls.get(i);
            MimeType mimeType = MimeType.fromContentType(vid.getVideo().getContentType());
            
            resources.add(Resource.builder()
                .lesson(Lesson.builder().id(lessonId).build())
                .resourceType(ResourceType.VIDEO)
                .fileUrl(url)
                .fileSize(vid.getVideo().getSize())
                .mimeType(mimeType)
                .orderPosition(vid.getOrderPosition())
                .build());
        }
        
        return resources;
    }

    private List<VideoMetaData> prepareMetas(BulkVideoUploadRequestDto dto, List<Resource> resources) {
        List<VideoMetaData> metas = new ArrayList<>();
        
        for (int i = 0; i < dto.getVideos().size(); i++) {
            VideoUploadRequestDto vid = dto.getVideos().get(i);
            Resource res = resources.get(i);
            
            metas.add(VideoMetaData.builder()
                .resource(res)
                .duration(vid.getDuration())
                .resolution(vid.getResolution())
                .build());
        }
        
        return metas;
    }

    private List<ResourceTranslation> processAllTranslations(BulkVideoUploadRequestDto dto, List<Resource> resources) {
        List<ResourceTranslation> translations = new ArrayList<>();
        
        for (int i = 0; i < dto.getVideos().size(); i++) {
            VideoUploadRequestDto vid = dto.getVideos().get(i);
            Resource res = resources.get(i);
            
            if (vid.getTranslations() != null) {
                for (ResourceTranslationRequestDto t : vid.getTranslations()) {
                    translations.add(ResourceTranslation.builder()
                        .resource(res)
                        .language(t.getLanguage())
                        .title(t.getTitle())
                        .description(t.getDescription())
                        .build());
                }
            }
        }
        
        return resourceTranslationService.saveAllTranslations(translations);
    }

    private void associateTranslationsWithResources(List<Resource> resources, List<ResourceTranslation> translations) {
        for (Resource res : resources) {
            List<ResourceTranslation> resourceTranslations = translations.stream()
                .filter(t -> t.getResource().getId().equals(res.getId()))
                .toList();
            res.setTranslations(resourceTranslations);
        }
    }

    private BulkVideoUploadResponseDto buildBulkResponse(List<Resource> resources, List<VideoMetaData> metas) {
        Map<UUID, VideoMetaData> metaMap = metas.stream()
            .collect(Collectors.toMap(m -> m.getResource().getId(), Function.identity()));

        List<VideoUploadResponseDto> videoDtos = resources.stream()
            .map(res -> {
                VideoMetaData m = metaMap.get(res.getId());
                return VideoUploadResponseDto.builder()
                    .resourceId(res.getId())
                    .fileUrl(res.getFileUrl())
                    .durationSeconds(m != null ? m.getDuration() : null)
                    .resolution(m != null ? m.getResolution() : null)
                    .orderPosition(res.getOrderPosition())
                    .translations(mapTranslationsToDto(res.getTranslations()))
                    .build();
            }).toList();

        return BulkVideoUploadResponseDto.builder()
            .videos(videoDtos)
            .build();
    }

    private List<ResourceTranslationResponseDto> mapTranslationsToDto(List<ResourceTranslation> translations) {
        if (translations == null) return List.of();
        
        return translations.stream()
            .map(t -> ResourceTranslationResponseDto.builder()
                .id(t.getId())
                .language(t.getLanguage())
                .title(t.getTitle())
                .description(t.getDescription())
                .build())
            .toList();
    }
}