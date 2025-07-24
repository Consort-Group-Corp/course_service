package uz.consortgroup.course_service.service.media.image;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.image.ImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.repository.ResourceRepository;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.media.AbstractMediaUploadService;
import uz.consortgroup.course_service.service.media.processor.image.BulkImageUploadProcessor;
import uz.consortgroup.course_service.service.media.processor.image.ImageUploadProcessor;
import uz.consortgroup.course_service.service.storage.FileStorageService;
import uz.consortgroup.course_service.validator.FileStorageValidator;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ImageServiceImpl extends AbstractMediaUploadService<ImageUploadRequestDto, ImageUploadResponseDto, BulkImageUploadRequestDto,
        BulkImageUploadResponseDto, ImageUploadProcessor, BulkImageUploadProcessor> implements ImageService {

    private final ResourceRepository resourceRepository;

    public ImageServiceImpl(FileStorageService storage, LessonService lessonService,
                            ImageUploadProcessor imageUploadProcessor, BulkImageUploadProcessor bulkImageUploadProcessor,
                            FileStorageValidator fileStorageValidator, ResourceRepository resourceRepository) {
        super(storage, lessonService, imageUploadProcessor, bulkImageUploadProcessor, fileStorageValidator);
        this.resourceRepository = resourceRepository;
    }

    @Override
    @Transactional
    public void delete(UUID lessonId, UUID resourceId) {
        log.info("Attempting to delete resource with id={} for lessonId={}", resourceId, lessonId);

        Resource res = resourceRepository.findByIdAndLesson(resourceId, lessonId)
                .orElseThrow(() -> {
                    log.warn("Resource not found for deletion: resourceId={}, lessonId={}", resourceId, lessonId);
                    return new EntityNotFoundException("Resource not found");
                });

        storage.delete(res.getFileUrl());
        resourceRepository.delete(res);

        log.info("Successfully deleted resource with id={} for lessonId={}", resourceId, lessonId);
    }

    @Override
    @Transactional
    public void deleteBulk(UUID lessonId, List<UUID> resourceIds) {
        log.info("Attempting to bulk delete {} resources for lessonId={}", resourceIds.size(), lessonId);

        List<Resource> resources = resourceRepository.findAllByIdsAndLesson(resourceIds, lessonId);

        for (Resource res : resources) {
            storage.delete(res.getFileUrl());
        }

        resourceRepository.deleteAll(resources);

        log.info("Successfully deleted {} resources for lessonId={}", resources.size(), lessonId);
    }
}
