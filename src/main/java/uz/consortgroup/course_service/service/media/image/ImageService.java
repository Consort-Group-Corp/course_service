package uz.consortgroup.course_service.service.media.image;

import uz.consortgroup.course_service.dto.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.course_service.dto.request.image.ImageUploadRequestDto;
import uz.consortgroup.course_service.dto.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.course_service.dto.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.service.media.processor.MediaUploadService;

public interface ImageService extends MediaUploadService<ImageUploadRequestDto, ImageUploadResponseDto, BulkImageUploadRequestDto, BulkImageUploadResponseDto> {
}
