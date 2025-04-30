package uz.consortgroup.course_service.service.media.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.dto.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.course_service.dto.request.image.ImageUploadRequestDto;
import uz.consortgroup.course_service.dto.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.course_service.dto.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.exception.EmptyFileException;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.media.processor.image.BulkImageUploadProcessor;
import uz.consortgroup.course_service.service.media.processor.image.ImageUploadProcessor;
import uz.consortgroup.course_service.service.storage.FileStorageService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {
    private final LessonService lessonService;
    private final FileStorageService fileStorageService;
    private final ImageUploadProcessor imageUploadProcessor;
    private final BulkImageUploadProcessor bulkImageUploadProcessor;

    @Override
    @Transactional
    @AllAspect
    public ImageUploadResponseDto upload(UUID lessonId, ImageUploadRequestDto dto) {
        if (dto.getImage() == null || dto.getImage().isEmpty()) {
            throw new EmptyFileException("Image file cannot be null or empty");
        }

        Lesson lesson = lessonService.getLessonEntity(lessonId);
        UUID courseId = lesson.getModule().getCourse().getId();

        String url = fileStorageService.store(courseId, lessonId, dto.getImage());

        MimeType mimeType = MimeType.fromContentType(dto.getImage().getContentType());

        return imageUploadProcessor.processSingle(lessonId, dto, url, mimeType);
    }

    @Override
    @Transactional
    @AllAspect
    public BulkImageUploadResponseDto uploadImages(UUID lessonId, BulkImageUploadRequestDto dto) {
        if (dto.getImages() == null || dto.getImages().isEmpty()) {
            throw new IllegalArgumentException("Image list cannot be null or empty");
        }

        Lesson lesson = lessonService.getLessonEntity(lessonId);
        UUID courseId = lesson.getModule().getCourse().getId();

        List<String> urls = fileStorageService.storeMultiple(courseId, lessonId, dto.getImages().stream()
                .map(ImageUploadRequestDto::getImage)
                .collect(Collectors.toList()));

        return bulkImageUploadProcessor.processBulkUpload(lessonId, dto, urls);
    }
}
