package uz.consortgroup.course_service.service.media.image;

import org.springframework.stereotype.Service;
import uz.consortgroup.course_service.dto.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.course_service.dto.request.image.ImageUploadRequestDto;
import uz.consortgroup.course_service.dto.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.course_service.dto.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.media.AbstractMediaUploadService;
import uz.consortgroup.course_service.service.media.processor.image.BulkImageUploadProcessor;
import uz.consortgroup.course_service.service.media.processor.image.ImageUploadProcessor;
import uz.consortgroup.course_service.service.storage.FileStorageService;
import uz.consortgroup.course_service.validator.FileStorageValidator;

@Service
public class ImageServiceImpl extends AbstractMediaUploadService<ImageUploadRequestDto, ImageUploadResponseDto, BulkImageUploadRequestDto,
        BulkImageUploadResponseDto, ImageUploadProcessor, BulkImageUploadProcessor> implements ImageService {

    public ImageServiceImpl(FileStorageService storage, LessonService lessonService, ImageUploadProcessor imageUploadProcessor, BulkImageUploadProcessor bulkImageUploadProcessor, FileStorageValidator fileStorageValidator) {
        super(storage, lessonService, imageUploadProcessor, bulkImageUploadProcessor, fileStorageValidator);
    }
}