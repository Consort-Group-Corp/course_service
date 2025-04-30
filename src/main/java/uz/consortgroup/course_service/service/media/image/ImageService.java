package uz.consortgroup.course_service.service.media.image;

import uz.consortgroup.course_service.dto.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.course_service.dto.request.image.ImageUploadRequestDto;
import uz.consortgroup.course_service.dto.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.course_service.dto.response.image.ImageUploadResponseDto;

import java.util.UUID;

public interface ImageService {
    ImageUploadResponseDto upload(UUID lessonId, ImageUploadRequestDto dto);
    BulkImageUploadResponseDto uploadImages(UUID lessonId, BulkImageUploadRequestDto dto);
}
