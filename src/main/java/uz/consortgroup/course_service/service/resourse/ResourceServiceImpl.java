package uz.consortgroup.course_service.service.resourse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.dto.request.lesson.LessonCreateRequestDto;
import uz.consortgroup.course_service.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.dto.request.resource.ResourceCreateRequestDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.entity.enumeration.ResourceType;
import uz.consortgroup.course_service.repository.ResourceRepository;
import uz.consortgroup.course_service.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final LessonRepository lessonRepository;

    @Override
    @Transactional
    @AllAspect
    public Resource create(UUID lessonId,
                           ResourceType resourceType,
                           String fileUrl,
                           Long fileSize,
                           MimeType mimeType,
                           Integer orderPosition) {

        Lesson lesson = lessonRepository.getReferenceById(lessonId);
        Resource res = Resource.builder()
                .lesson(lesson)
                .resourceType(resourceType)
                .fileUrl(fileUrl)
                .fileSize(fileSize)
                .mimeType(mimeType)
                .orderPosition(orderPosition)
                .build();
        return resourceRepository.save(res);
    }

    @Override
    @Transactional
    @AllAspect
    public List<Resource> createBulk(List<ModuleCreateRequestDto> moduleDtos, List<Lesson> lessons) {
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

        return resourceRepository.saveAll(resources);
    }


    @Transactional
    @AllAspect
    public List<Resource> saveAllResources(List<Resource> resources) {
        return resourceRepository.saveAll(resources);
    }
}
