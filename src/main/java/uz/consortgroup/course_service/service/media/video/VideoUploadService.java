package uz.consortgroup.course_service.service.media.video;

import uz.consortgroup.core.api.v1.dto.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.request.video.VideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.service.media.processor.MediaUploadService;

public interface VideoUploadService extends MediaUploadService<VideoUploadRequestDto, VideoUploadResponseDto, BulkVideoUploadRequestDto, BulkVideoUploadResponseDto> {
}
