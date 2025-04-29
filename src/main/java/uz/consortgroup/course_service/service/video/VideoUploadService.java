package uz.consortgroup.course_service.service.video;

import uz.consortgroup.course_service.dto.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.course_service.dto.request.video.VideoUploadRequestDto;
import uz.consortgroup.course_service.dto.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.course_service.dto.response.video.VideoUploadResponseDto;

import java.util.UUID;

public interface VideoUploadService {
    VideoUploadResponseDto upload(UUID lessonId, VideoUploadRequestDto dto);
    BulkVideoUploadResponseDto uploadVideos(UUID lessonId, BulkVideoUploadRequestDto dto);
}
