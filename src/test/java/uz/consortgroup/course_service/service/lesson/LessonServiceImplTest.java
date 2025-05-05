package uz.consortgroup.course_service.service.lesson;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.course_service.dto.request.lesson.LessonCreateRequestDto;
import uz.consortgroup.course_service.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.entity.enumeration.LessonType;
import uz.consortgroup.course_service.exception.LessonNotFoundException;
import uz.consortgroup.course_service.repository.LessonRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LessonServiceImplTest {
    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private LessonServiceImpl lessonService;

    @Test
    void saveLessons_WithValidData_ShouldReturnSavedLessons() {
        ModuleCreateRequestDto moduleDto = new ModuleCreateRequestDto();
        LessonCreateRequestDto lessonDto = new LessonCreateRequestDto();
        lessonDto.setOrderPosition(1);
        lessonDto.setContentUrl("content-url");
        lessonDto.setLessonType(LessonType.VIDEO);
        lessonDto.setIsPreview(true);
        lessonDto.setDurationMinutes(30);
        moduleDto.setLessons(List.of(lessonDto));

        Module module = new Module();
        module.setId(UUID.randomUUID());

        Lesson expectedLesson = Lesson.builder()
                .module(module)
                .orderPosition(1)
                .contentUrl("content-url")
                .lessonType(LessonType.VIDEO)
                .isPreview(true)
                .durationMinutes(30)
                .build();

        when(lessonRepository.saveAll(anyList())).thenReturn(List.of(expectedLesson));

        List<Lesson> result = lessonService.saveLessons(List.of(moduleDto), List.of(module));

        assertEquals(1, result.size());
        assertEquals("content-url", result.get(0).getContentUrl());
        verify(lessonRepository).saveAll(anyList());
    }

    @Test
    void saveLessons_WithEmptyList_ShouldReturnEmptyList() {
        List<Lesson> result = lessonService.saveLessons(Collections.emptyList(), Collections.emptyList());

        assertTrue(result.isEmpty());
        verify(lessonRepository, times(1)).saveAll(anyList());
    }

    @Test
    void findByModuleId_WithValidId_ShouldReturnLessons() {
        UUID moduleId = UUID.randomUUID();
        Lesson lesson = new Lesson();
        lesson.setId(UUID.randomUUID());

        when(lessonRepository.findAllByModuleId(moduleId)).thenReturn(List.of(lesson));

        List<Lesson> result = lessonService.findByModuleId(moduleId);

        assertEquals(1, result.size());
        verify(lessonRepository).findAllByModuleId(moduleId);
    }

    @Test
    void findByModuleId_WithNonExistingId_ShouldReturnEmptyList() {
        UUID nonExistingId = UUID.randomUUID();

        when(lessonRepository.findAllByModuleId(nonExistingId)).thenReturn(Collections.emptyList());

        List<Lesson> result = lessonService.findByModuleId(nonExistingId);

        assertTrue(result.isEmpty());
        verify(lessonRepository).findAllByModuleId(nonExistingId);
    }

    @Test
    void getLessonEntity_WithExistingId_ShouldReturnLesson() {
        UUID lessonId = UUID.randomUUID();
        Lesson expectedLesson = new Lesson();
        expectedLesson.setId(lessonId);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(expectedLesson));

        Lesson result = lessonService.getLessonEntity(lessonId);

        assertEquals(lessonId, result.getId());
        verify(lessonRepository).findById(lessonId);
    }

    @Test
    void getLessonEntity_WithNonExistingId_ShouldThrowException() {
        UUID nonExistingId = UUID.randomUUID();

        when(lessonRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(LessonNotFoundException.class, () -> lessonService.getLessonEntity(nonExistingId));
        verify(lessonRepository).findById(nonExistingId);
    }
}
