package uz.consortgroup.course_service.service.media.video;

import org.springframework.stereotype.Service;
import uz.consortgroup.core.api.v1.dto.course.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.video.VideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.media.AbstractMediaUploadService;
import uz.consortgroup.course_service.service.media.processor.video.BulkVideoUploadProcessor;
import uz.consortgroup.course_service.service.media.processor.video.VideoUploadProcessor;
import uz.consortgroup.course_service.service.storage.FileStorageService;
import uz.consortgroup.course_service.validator.FileStorageValidator;

@Service
public class VideoUploadServiceImpl extends AbstractMediaUploadService<VideoUploadRequestDto, VideoUploadResponseDto,
        BulkVideoUploadRequestDto, BulkVideoUploadResponseDto, VideoUploadProcessor, BulkVideoUploadProcessor> implements VideoUploadService {

    public VideoUploadServiceImpl(FileStorageService storage, LessonService lessonService, VideoUploadProcessor videoUploadProcessor, BulkVideoUploadProcessor bulkVideoUploadProcessor, FileStorageValidator fileStorageValidator) {
        super(storage, lessonService, videoUploadProcessor, bulkVideoUploadProcessor, fileStorageValidator);
    }
}