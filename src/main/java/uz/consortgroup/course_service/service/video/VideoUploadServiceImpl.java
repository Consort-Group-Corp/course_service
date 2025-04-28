package uz.consortgroup.course_service.service.video;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
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
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.ResourceTranslationService;
import uz.consortgroup.course_service.service.storage.FileStorageService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoUploadServiceImpl implements VideoUploadService {
    private final FileStorageService storage;
    private final LessonService lessonService;
    private final ResourceService resourceService;
    private final VideoMetadataService videoMetadataService;
    private final ResourceTranslationService resourceTranslationService; // Добавляем зависимость

    @Override
    @Transactional
    @AllAspect
    public VideoUploadResponseDto upload(UUID lessonId, VideoUploadRequestDto dto) {
        Lesson lesson = lessonService.getLessonEntity(lessonId);
        UUID courseId = lesson.getModule().getCourse().getId();

        String url = storage.store(courseId, lessonId, dto.getVideo());

        MimeType mimeType = MimeType.fromContentType(dto.getVideo().getContentType());

        Resource res = resourceService.create(
                lessonId,
                ResourceType.VIDEO,
                url,
                dto.getVideo().getSize(),
                mimeType,
                dto.getOrderPosition()
        );

        // Создаем переводы
        List<ResourceTranslation> translations = new ArrayList<>();
        if (dto.getTranslations() != null) {
            translations = dto.getTranslations().stream()
                    .map(t -> ResourceTranslation.builder()
                            .resource(res)
                            .language(t.getLanguage())
                            .title(t.getTitle())
                            .description(t.getDescription())
                            .build())
                    .toList();
            translations = resourceTranslationService.saveAllTranslations(translations);
        }

        // Привязываем переводы к ресурсу
        res.setTranslations(translations);

        VideoMetaData meta = videoMetadataService.create(
                res.getId(),
                dto.getDuration(),
                dto.getResolution()
        );

        return VideoUploadResponseDto.builder()
                .resourceId(res.getId())
                .fileUrl(res.getFileUrl())
                .durationSeconds(meta.getDuration())
                .resolution(meta.getResolution())
                .orderPosition(res.getOrderPosition())
                .translations(
                        translations.stream()
                                .map(t -> ResourceTranslationResponseDto.builder()
                                        .id(t.getId())
                                        .language(t.getLanguage())
                                        .title(t.getTitle())
                                        .description(t.getDescription())
                                        .build())
                                .toList())
                .build();
    }

    @Transactional
    @AllAspect
    public BulkVideoUploadResponseDto uploadVideos(UUID lessonId, BulkVideoUploadRequestDto dto) {
        Lesson lesson = lessonService.getLessonEntity(lessonId);
        UUID courseId = lesson.getModule().getCourse().getId();

        List<Resource> resources = new ArrayList<>();
        List<VideoMetaData> metas = new ArrayList<>();
        List<ResourceTranslation> translations = new ArrayList<>();

        List<String> urls = storage.storeMultiple(
                courseId,
                lessonId,
                dto.getVideos().stream()
                        .map(VideoUploadRequestDto::getVideo)
                        .collect(Collectors.toList())
        );

        // Создаем ресурсы и метаданные
        for (int i = 0; i < dto.getVideos().size(); i++) {
            VideoUploadRequestDto vid = dto.getVideos().get(i);
            String url = urls.get(i);
            MimeType mimeType = MimeType.fromContentType(vid.getVideo().getContentType());

            Resource res = Resource.builder()
                    .lesson(lesson)
                    .resourceType(ResourceType.VIDEO)
                    .fileUrl(url)
                    .fileSize(vid.getVideo().getSize())
                    .mimeType(mimeType)
                    .orderPosition(vid.getOrderPosition())
                    .build();
            resources.add(res);

            VideoMetaData meta = VideoMetaData.builder()
                    .resource(res)
                    .duration(vid.getDuration())
                    .resolution(vid.getResolution())
                    .build();
            metas.add(meta);
        }

        // Сохраняем ресурсы и метаданные
        resources = resourceService.saveAllResources(resources);
        videoMetadataService.saveAll(metas);

        // Создаем переводы после сохранения ресурсов
        for (int i = 0; i < dto.getVideos().size(); i++) {
            VideoUploadRequestDto vid = dto.getVideos().get(i);
            Resource res = resources.get(i);

            if (vid.getTranslations() != null) {
                for (ResourceTranslationRequestDto t : vid.getTranslations()) {
                    translations.add(ResourceTranslation.builder()
                            .resource(res) // Ссылаемся на уже сохраненный ресурс
                            .language(t.getLanguage())
                            .title(t.getTitle())
                            .description(t.getDescription())
                            .build());
                }
            }
        }

        // Сохраняем переводы
        translations = resourceTranslationService.saveAllTranslations(translations);

        // Привязываем переводы к ресурсам
        for (Resource res : resources) {
            List<ResourceTranslation> resourceTranslations = translations.stream()
                    .filter(t -> t.getResource().getId().equals(res.getId()))
                    .toList();
            res.setTranslations(resourceTranslations);
        }

        return buildBulkResponse(resources, metas);
    }

    private BulkVideoUploadResponseDto buildBulkResponse(List<Resource> resources, List<VideoMetaData> metas) {
        Map<UUID, VideoMetaData> metaMap = metas.stream()
                .collect(Collectors.toMap(m -> m.getResource().getId(), Function.identity()));

        List<VideoUploadResponseDto> videoDtos = resources.stream()
                .map(res -> {
                    VideoMetaData m = metaMap.get(res.getId());
                    List<ResourceTranslationResponseDto> trDtos = Optional.ofNullable(res.getTranslations())
                            .orElseGet(List::of)
                            .stream()
                            .map(t -> ResourceTranslationResponseDto.builder()
                                    .id(t.getId())
                                    .language(t.getLanguage())
                                    .title(t.getTitle())
                                    .description(t.getDescription())
                                    .build())
                            .toList();

                    return VideoUploadResponseDto.builder()
                            .resourceId(res.getId())
                            .fileUrl(res.getFileUrl())
                            .durationSeconds(m != null ? m.getDuration() : null)
                            .resolution(m != null ? m.getResolution() : null)
                            .orderPosition(res.getOrderPosition())
                            .translations(trDtos)
                            .build();
                }).toList();

        return BulkVideoUploadResponseDto.builder()
                .videos(videoDtos)
                .build();
    }
}
