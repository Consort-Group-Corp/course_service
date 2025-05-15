package uz.consortgroup.course_service.service.media.image;


import uz.consortgroup.core.api.v1.dto.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.request.image.ImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.service.media.processor.MediaUploadService;

public interface ImageService extends MediaUploadService<ImageUploadRequestDto, ImageUploadResponseDto, BulkImageUploadRequestDto, BulkImageUploadResponseDto> {
}
