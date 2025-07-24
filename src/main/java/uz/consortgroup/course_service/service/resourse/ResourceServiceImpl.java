package uz.consortgroup.course_service.service.resourse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.enumeration.MimeType;
import uz.consortgroup.core.api.v1.dto.course.enumeration.ResourceType;
import uz.consortgroup.core.api.v1.dto.course.request.lesson.LessonCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.repository.ResourceRepository;
import uz.consortgroup.course_service.service.lesson.LessonService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final LessonService lessonService;

    @Override
    @Transactional
    public Resource create(UUID lessonId, ResourceType resourceType, String fileUrl, Long fileSize, MimeType mimeType, Integer orderPosition) {
        log.info("Creating resource for lessonId={} with mimeType={} and size={} bytes", lessonId, mimeType, fileSize);

        Lesson lesson = lessonService.getLessonEntity(lessonId);
        Resource res = Resource.builder()
                .lesson(lesson)
                .resourceType(resourceType)
                .fileUrl(fileUrl)
                .fileSize(fileSize)
                .mimeType(mimeType)
                .orderPosition(orderPosition)
                .build();
        Resource saved = resourceRepository.save(res);

        log.debug("Saved resource with id={}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public List<Resource> createBulk(List<ModuleCreateRequestDto> moduleDtos, List<Lesson> lessons) {
        log.info("Creating bulk resources for {} modules", moduleDtos.size());

        List<Resource> resources = new ArrayList<>();
        int lessonIndex = 0;

        for (ModuleCreateRequestDto moduleDto : moduleDtos) {
            for (LessonCreateRequestDto lessonDto : moduleDto.getLessons()) {
                Lesson lesson = lessons.get(lessonIndex++);
                if (lessonDto.getResources() != null && !lessonDto.getResources().isEmpty()) {
                    List<Resource> lessonResources = lessonDto.getResources().stream()
                            .map(resourceDto -> Resource.builder()
                                    .lesson(lesson)
                                    .resourceType(resourceDto.getResourceType())
                                    .fileUrl(resourceDto.getFileUrl())
                                    .fileSize(resourceDto.getFileSize())
                                    .mimeType(resourceDto.getMimeType())
                                    .orderPosition(resourceDto.getOrderPosition())
                                    .build())
                            .toList();
                    resources.addAll(lessonResources);
                }
            }
        }

        List<Resource> saved = resourceRepository.saveAll(resources);
        log.debug("Saved {} resources in bulk", saved.size());
        return saved;
    }

    @Transactional
    public List<Resource> saveAllResources(List<Resource> resources) {
        log.info("Saving {} resources", resources.size());
        List<Resource> saved = resourceRepository.saveAll(resources);
        log.debug("Saved {} resources", saved.size());
        return saved;
    }
}
