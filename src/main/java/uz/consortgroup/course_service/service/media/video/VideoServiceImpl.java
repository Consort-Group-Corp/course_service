package uz.consortgroup.course_service.service.media.video;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.video.VideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
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

@Service
public class VideoServiceImpl extends AbstractMediaUploadService<VideoUploadRequestDto, VideoUploadResponseDto,
        BulkVideoUploadRequestDto, BulkVideoUploadResponseDto, VideoUploadProcessor, BulkVideoUploadProcessor> implements VideoService {

    private final ResourceRepository resourceRepository;
    private final VideoMetaDataRepository videoMetaDataRepository;

    public VideoServiceImpl(FileStorageService storage, LessonService lessonService,
                            VideoUploadProcessor videoUploadProcessor, BulkVideoUploadProcessor bulkVideoUploadProcessor,
                            FileStorageValidator fileStorageValidator, ResourceRepository resourceRepository, VideoMetaDataRepository videoMetaDataRepository) {
        super(storage, lessonService, videoUploadProcessor, bulkVideoUploadProcessor, fileStorageValidator);
        this.resourceRepository = resourceRepository;
        this.videoMetaDataRepository = videoMetaDataRepository;
    }

    @Override
    @Transactional
    @AllAspect
    public void delete(UUID lessonId, UUID resourceId) {
        Resource res = resourceRepository
                .findByIdAndLesson(resourceId, lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Video resource not found"));

        videoMetaDataRepository.deleteByResource_Id(resourceId);

        storage.delete(res.getFileUrl());
        resourceRepository.delete(res);

    }

    @Override
    @Transactional
    @AllAspect
    public void deleteBulk(UUID lessonId, List<UUID> resourceIds) {
        List<Resource> resources = resourceRepository.findAllByIdsAndLesson(resourceIds, lessonId);

        for (Resource res : resources) {
            UUID id = res.getId();
            videoMetaDataRepository.deleteByResource_Id(id);
            storage.delete(res.getFileUrl());
        }

        resourceRepository.deleteAll(resources);

        if (resources.size() != resourceIds.size()) {
            throw new EntityNotFoundException("Some video resources were not found for lesson " + lessonId);
        }
    }
}