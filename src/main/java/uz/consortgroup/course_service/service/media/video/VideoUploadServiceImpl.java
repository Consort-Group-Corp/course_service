package uz.consortgroup.course_service.service.media.video;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.dto.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.course_service.dto.request.video.VideoUploadRequestDto;
import uz.consortgroup.course_service.dto.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.course_service.dto.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.media.processor.video.BulkVideoUploadProcessor;
import uz.consortgroup.course_service.service.media.processor.video.VideoUploadProcessor;
import uz.consortgroup.course_service.service.storage.FileStorageService;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoUploadServiceImpl implements VideoUploadService {
    private final FileStorageService storage;
    private final LessonService lessonService;
    private final VideoUploadProcessor videoUploadProcessor;
    private final BulkVideoUploadProcessor bulkVideoUploadProcessor;


    @Override
    @Transactional
    @AllAspect
    public VideoUploadResponseDto upload(UUID lessonId, VideoUploadRequestDto dto) {
        Lesson lesson = lessonService.getLessonEntity(lessonId);
        UUID courseId = lesson.getModule().getCourse().getId();

        String url = storage.store(courseId, lessonId, dto.getVideo());
        MimeType mimeType = MimeType.fromContentType(dto.getVideo().getContentType());

        return videoUploadProcessor.processSingle(lessonId, dto, url, mimeType);
    }

    @Override
    @Transactional
    @AllAspect
    public BulkVideoUploadResponseDto uploadVideos(UUID lessonId, BulkVideoUploadRequestDto dto) {
        Lesson lesson = lessonService.getLessonEntity(lessonId);
        UUID courseId = lesson.getModule().getCourse().getId();

        List<String> urls = storage.storeMultiple(courseId, lessonId, dto.getVideos().stream()
                        .map(VideoUploadRequestDto::getVideo)
                        .collect(Collectors.toList()));

        return bulkVideoUploadProcessor.processBulkUpload(lessonId, dto, urls);
    }
}
