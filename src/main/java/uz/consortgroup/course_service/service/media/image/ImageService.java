package uz.consortgroup.course_service.service.media.image;


import uz.consortgroup.core.api.v1.dto.course.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.image.ImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.service.media.MediaService;

public interface ImageService extends MediaService<ImageUploadRequestDto, ImageUploadResponseDto, BulkImageUploadRequestDto, BulkImageUploadResponseDto> {
}
