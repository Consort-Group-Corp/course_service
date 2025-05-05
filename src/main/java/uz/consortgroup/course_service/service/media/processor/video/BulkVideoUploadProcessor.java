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
import uz.consortgroup.course_service.service.media.video.metadate.VideoMetadataService;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.translation.ResourceTranslationService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BulkVideoUploadProcessor extends AbstractMediaUploadProcessor<VideoUploadRequestDto, VideoUploadResponseDto,
        BulkVideoUploadRequestDto, BulkVideoUploadResponseDto> {
    private final VideoMetadataService videoMetadataService;

    public BulkVideoUploadProcessor(ResourceService resourceService, ResourceTranslationService translationService, ResourceTranslationMapper translationMapper, VideoMetadataService videoMetadataService) {
        super(resourceService, translationService, translationMapper);
        this.videoMetadataService = videoMetadataService;
    }

    @Override
    protected List<VideoUploadRequestDto> extractDtos(BulkVideoUploadRequestDto bulkDto) {
        return bulkDto.getVideos();
    }

    @Override
    protected Resource createResource(UUID lessonId, VideoUploadRequestDto dto, String fileUrl, MimeType mimeType, long fileSize) {
        throw new UnsupportedOperationException("This method is not used in bulk upload processor");
    }

    @Override
    protected List<Resource> prepareResources(UUID lessonId, List<VideoUploadRequestDto> dtos, List<String> fileUrls, List<MimeType> mimeTypes, List<Long> fileSizes) {
        List<Resource> resources = new ArrayList<>();
        for (int i = 0; i < dtos.size(); i++) {
            VideoUploadRequestDto vid = dtos.get(i);
            String fileUrl = fileUrls.get(i);
            MimeType mimeType = mimeTypes.get(i);
            long fileSize = fileSizes.get(i);
            resources.add(Resource.builder()
                    .lesson(Lesson.builder().id(lessonId).build())
                    .resourceType(ResourceType.VIDEO)
                    .fileUrl(fileUrl)
                    .fileSize(fileSize)
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
            if (vid.getTranslations() != null && !vid.getTranslations().isEmpty()) {
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
    protected VideoUploadResponseDto buildSingleResponse(Resource resource, VideoUploadRequestDto dto) {
        throw new UnsupportedOperationException("This method is not used in bulk upload processor");
    }

    @Override
    protected BulkVideoUploadResponseDto buildBulkResponse(List<Resource> resources, List<VideoUploadRequestDto> dtos) {
        List<VideoUploadResponseDto> videoDtos = new ArrayList<>();
        for (int i = 0; i < resources.size(); i++) {
            Resource res = resources.get(i);
            VideoUploadRequestDto vid = dtos.get(i);
            VideoMetaData meta = videoMetadataService.create(res.getId(), vid.getDuration(), vid.getResolution());
            List<ResourceTranslation> translations = translationService.findResourceTranslationById(res.getId());
            videoDtos.add(VideoUploadResponseDto.builder()
                    .resourceId(res.getId())
                    .fileUrl(res.getFileUrl())
                    .durationSeconds(meta.getDuration())
                    .resolution(meta.getResolution())
                    .orderPosition(res.getOrderPosition())
                    .translations(mapTranslations(translations))
                    .build());
        }
        return BulkVideoUploadResponseDto.builder()
                .videos(videoDtos)
                .build();
    }
}