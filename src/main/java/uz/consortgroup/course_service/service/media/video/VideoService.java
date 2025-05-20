package uz.consortgroup.course_service.service.media.video;

import uz.consortgroup.core.api.v1.dto.course.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.video.VideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.service.media.MediaService;

public interface VideoService extends MediaService<VideoUploadRequestDto, VideoUploadResponseDto, BulkVideoUploadRequestDto, BulkVideoUploadResponseDto> {
}
