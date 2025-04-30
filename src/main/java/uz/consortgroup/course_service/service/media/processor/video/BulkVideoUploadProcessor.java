package uz.consortgroup.course_service.service.media.processor.video;

import org.springframework.stereotype.Service;
import uz.consortgroup.course_service.dto.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.dto.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.course_service.dto.request.video.VideoUploadRequestDto;
import uz.consortgroup.course_service.dto.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.course_service.dto.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.entity.Lesson;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BulkVideoUploadProcessor extends AbstractMediaUploadProcessor<VideoUploadRequestDto, Void, BulkVideoUploadResponseDto> {
    private final VideoMetadataService videoMetadataService;

    public BulkVideoUploadProcessor(ResourceService resourceService, ResourceTranslationService translationService, ResourceTranslationMapper translationMapper, VideoMetadataService videoMetadataService) {
        super(resourceService, translationService, translationMapper);
        this.videoMetadataService = videoMetadataService;
    }

    @Override
    protected Resource createResource(UUID lessonId, VideoUploadRequestDto dto, String fileUrl, MimeType mimeType) {
        throw new UnsupportedOperationException("This method is not used in bulk upload processor");
    }

    @Override
    protected List<Resource> prepareResources(UUID lessonId, List<VideoUploadRequestDto> dtos, List<String> fileUrls) {
        List<Resource> resources = new ArrayList<>();
        for (int i = 0; i < dtos.size(); i++) {
            VideoUploadRequestDto vid = dtos.get(i);
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

    @Override
    protected void saveTranslations(VideoUploadRequestDto dto, Resource resource) {
        throw new UnsupportedOperationException("This method is not used in bulk upload processor");
    }

    @Override
    protected void saveAllTranslations(List<VideoUploadRequestDto> dtos, List<Resource> resources) {
        List<ResourceTranslation> translations = new ArrayList<>();
        for (int i = 0; i < dtos.size(); i++) {
            VideoUploadRequestDto vid = dtos.get(i);
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
        translationService.saveAllTranslations(translations);
    }

    @Override
    protected Void buildSingleResponse(Resource resource, VideoUploadRequestDto dto) {
        throw new UnsupportedOperationException("This method is not used in bulk upload processor");
    }

    @Override
    protected BulkVideoUploadResponseDto buildBulkResponse(List<Resource> resources, List<VideoUploadRequestDto> dtos) {
        List<VideoMetaData> metas = new ArrayList<>();
        for (int i = 0; i < resources.size(); i++) {
            Resource res = resources.get(i);
            VideoUploadRequestDto dto = dtos.get(i);
            metas.add(VideoMetaData.builder()
                    .resource(res)
                    .duration(dto.getDuration())
                    .resolution(dto.getResolution())
                    .build());
        }
        videoMetadataService.saveAll(metas);

        Map<UUID, VideoMetaData> metaMap = metas.stream()
                .collect(Collectors.toMap(m -> m.getResource().getId(), Function.identity()));

        List<VideoUploadResponseDto> videoDtos = resources.stream()
                .map(res -> {
                    VideoMetaData m = metaMap.get(res.getId());
                    List<ResourceTranslation> translations = translationService.findResourceTranslationById(res.getId());
                    return VideoUploadResponseDto.builder()
                            .resourceId(res.getId())
                            .fileUrl(res.getFileUrl())
                            .durationSeconds(m != null ? m.getDuration() : null)
                            .resolution(m != null ? m.getResolution() : null)
                            .orderPosition(res.getOrderPosition())
                            .translations(mapTranslations(translations))
                            .build();
                }).toList();

        return BulkVideoUploadResponseDto.builder()
                .videos(videoDtos)
                .build();
    }

    public BulkVideoUploadResponseDto processBulkUpload(UUID lessonId, BulkVideoUploadRequestDto dto, List<String> fileUrls) {
        return processBulk(lessonId, dto.getVideos(), fileUrls);
    }
}