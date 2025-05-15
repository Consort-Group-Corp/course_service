package uz.consortgroup.course_service.service.media.video;

import uz.consortgroup.core.api.v1.dto.course.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.video.VideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.service.media.processor.MediaUploadService;

public interface VideoUploadService extends MediaUploadService<VideoUploadRequestDto, VideoUploadResponseDto, BulkVideoUploadRequestDto, BulkVideoUploadResponseDto> {
}
