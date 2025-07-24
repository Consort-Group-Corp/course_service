package uz.consortgroup.course_service.service.media.video;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.video.VideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.repository.ResourceRepository;
import uz.consortgroup.course_service.repository.VideoMetaDataRepository;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.media.AbstractMediaUploadService;
import uz.consortgroup.course_service.service.media.processor.video.BulkVideoUploadProcessor;
import uz.consortgroup.course_service.service.media.processor.video.VideoUploadProcessor;
import uz.consortgroup.course_service.service.storage.FileStorageService;
import uz.consortgroup.course_service.validator.FileStorageValidator;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class VideoServiceImpl extends AbstractMediaUploadService<VideoUploadRequestDto, VideoUploadResponseDto,
        BulkVideoUploadRequestDto, BulkVideoUploadResponseDto, VideoUploadProcessor, BulkVideoUploadProcessor> implements VideoService {

    private final ResourceRepository resourceRepository;
    private final VideoMetaDataRepository videoMetaDataRepository;

    public VideoServiceImpl(FileStorageService storage,
                            LessonService lessonService,
                            VideoUploadProcessor videoUploadProcessor,
                            BulkVideoUploadProcessor bulkVideoUploadProcessor,
                            FileStorageValidator fileStorageValidator,
                            ResourceRepository resourceRepository,
                            VideoMetaDataRepository videoMetaDataRepository) {
        super(storage, lessonService, videoUploadProcessor, bulkVideoUploadProcessor, fileStorageValidator);
        this.resourceRepository = resourceRepository;
        this.videoMetaDataRepository = videoMetaDataRepository;
    }

    @Override
    @Transactional
    public void delete(UUID lessonId, UUID resourceId) {
        log.info("Attempting to delete video with resourceId={} for lessonId={}", resourceId, lessonId);

        Resource res = resourceRepository
                .findByIdAndLesson(resourceId, lessonId)
                .orElseThrow(() -> {
                    log.warn("Video resource not found with id={} for lessonId={}", resourceId, lessonId);
                    return new EntityNotFoundException("Video resource not found");
                });

        videoMetaDataRepository.deleteByResource_Id(resourceId);
        storage.delete(res.getFileUrl());
        resourceRepository.delete(res);

        log.info("Successfully deleted video resourceId={} for lessonId={}", resourceId, lessonId);
    }

    @Override
    @Transactional
    public void deleteBulk(UUID lessonId, List<UUID> resourceIds) {
        log.info("Bulk deleting video resources for lessonId={}, resourceIds={}", lessonId, resourceIds);

        List<Resource> resources = resourceRepository.findAllByIdsAndLesson(resourceIds, lessonId);

        for (Resource res : resources) {
            UUID id = res.getId();
            log.debug("Deleting metadata and file for resourceId={} with URL={}", id, res.getFileUrl());
            videoMetaDataRepository.deleteByResource_Id(id);
            storage.delete(res.getFileUrl());
        }

        resourceRepository.deleteAll(resources);

        if (resources.size() != resourceIds.size()) {
            log.warn("Mismatch in expected vs found resources. Found={}, Expected={}", resources.size(), resourceIds.size());
            throw new EntityNotFoundException("Some video resources were not found for lesson " + lessonId);
        }

        log.info("Successfully deleted all specified video resources for lessonId={}", lessonId);
    }
}
