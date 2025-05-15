package uz.consortgroup.course_service.service.resourse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.course.enumeration.MimeType;
import uz.consortgroup.core.api.v1.dto.course.enumeration.ResourceType;
import uz.consortgroup.core.api.v1.dto.course.request.lesson.LessonCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.resource.ResourceCreateRequestDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.repository.ResourceRepository;
import uz.consortgroup.course_service.service.lesson.LessonService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void create_ShouldReturnSavedResource() {
        UUID lessonId = UUID.randomUUID();
        ResourceType resourceType = ResourceType.ARCHIVE;
        String fileUrl = "http://example.com/file.pdf";
        Long fileSize = 1024L;
        MimeType mimeType = MimeType.APPLICATION_PDF;
        Integer orderPosition = 1;

        Lesson lesson = new Lesson();
        Resource expectedResource = Resource.builder()
                .lesson(lesson)
                .resourceType(resourceType)
                .fileUrl(fileUrl)
                .fileSize(fileSize)
                .mimeType(mimeType)
                .orderPosition(orderPosition)
                .build();

        when(lessonService.getLessonEntity(lessonId)).thenReturn(lesson);
        when(resourceRepository.save(any(Resource.class))).thenReturn(expectedResource);

        Resource result = resourceService.create(lessonId, resourceType, fileUrl, fileSize, mimeType, orderPosition);

        assertNotNull(result);
        assertEquals(lesson, result.getLesson());
        assertEquals(fileUrl, result.getFileUrl());
        verify(resourceRepository).save(any(Resource.class));
    }

    @Test
    void createBulk_WithValidData_ShouldReturnSavedResources() {
        ModuleCreateRequestDto moduleDto = new ModuleCreateRequestDto();
        LessonCreateRequestDto lessonDto = new LessonCreateRequestDto();
        ResourceCreateRequestDto resourceDto = new ResourceCreateRequestDto();
        resourceDto.setResourceType(ResourceType.ARCHIVE);
        resourceDto.setFileUrl("url1");
        resourceDto.setFileSize(1024L);
        resourceDto.setMimeType(MimeType.APPLICATION_PDF);
        resourceDto.setOrderPosition(1);
        lessonDto.setResources(List.of(resourceDto));
        moduleDto.setLessons(List.of(lessonDto));

        Lesson lesson = new Lesson();
        Resource expectedResource = Resource.builder()
                .lesson(lesson)
                .resourceType(ResourceType.ARCHIVE)
                .fileUrl("url1")
                .fileSize(1024L)
                .mimeType(MimeType.APPLICATION_PDF)
                .orderPosition(1)
                .build();

        when(resourceRepository.saveAll(anyList())).thenReturn(List.of(expectedResource));

        List<Resource> result = resourceService.createBulk(List.of(moduleDto), List.of(lesson));

        assertEquals(1, result.size());
        verify(resourceRepository).saveAll(anyList());
    }

    @Test
    void createBulk_WithEmptyResources_ShouldReturnEmptyList() {
        ModuleCreateRequestDto moduleDto = new ModuleCreateRequestDto();
        LessonCreateRequestDto lessonDto = new LessonCreateRequestDto();
        lessonDto.setResources(Collections.emptyList());
        moduleDto.setLessons(List.of(lessonDto));

        Lesson lesson = new Lesson();

        List<Resource> result = resourceService.createBulk(List.of(moduleDto), List.of(lesson));

        assertTrue(result.isEmpty());
        verify(resourceRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createBulk_WithNullResources_ShouldReturnEmptyList() {
        ModuleCreateRequestDto moduleDto = new ModuleCreateRequestDto();
        LessonCreateRequestDto lessonDto = new LessonCreateRequestDto();
        lessonDto.setResources(null);
        moduleDto.setLessons(List.of(lessonDto));

        Lesson lesson = new Lesson();

        List<Resource> result = resourceService.createBulk(List.of(moduleDto), List.of(lesson));

        assertTrue(result.isEmpty());
        verify(resourceRepository, times(1)).saveAll(anyList());
    }

    @Test
    void saveAllResources_ShouldReturnSavedResources() {
        Resource resource1 = new Resource();
        Resource resource2 = new Resource();
        List<Resource> resources = List.of(resource1, resource2);
        List<Resource> expectedSavedResources = List.of(resource1, resource2);

        when(resourceRepository.saveAll(resources)).thenReturn(expectedSavedResources);

        List<Resource> result = resourceService.saveAllResources(resources);

        assertEquals(2, result.size());
        verify(resourceRepository).saveAll(resources);
    }
}