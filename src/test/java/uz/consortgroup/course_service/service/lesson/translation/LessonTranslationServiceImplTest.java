package uz.consortgroup.course_service.service.lesson.translation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.course_service.dto.request.lesson.LessonCreateRequestDto;
import uz.consortgroup.course_service.dto.request.lesson.LessonTranslationRequestDto;
import uz.consortgroup.course_service.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.LessonTranslation;
import uz.consortgroup.course_service.entity.enumeration.Language;
import uz.consortgroup.course_service.repository.LessonTranslationRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LessonTranslationServiceImplTest {

    @Mock
    private LessonTranslationRepository lessonTranslationRepository;

    @InjectMocks
    private LessonTranslationServiceImpl lessonTranslationService;

    @Test
    void saveTranslations_WithValidData_ShouldSaveTranslations() {
        ModuleCreateRequestDto moduleDto = new ModuleCreateRequestDto();
        LessonCreateRequestDto lessonDto = new LessonCreateRequestDto();
        LessonTranslationRequestDto translationDto = new LessonTranslationRequestDto();
        translationDto.setLanguage(Language.ENGLISH);
        translationDto.setTitle("Title");
        translationDto.setDescription("Description");
        lessonDto.setTranslations(List.of(translationDto));
        moduleDto.setLessons(List.of(lessonDto));

        Lesson lesson = new Lesson();
        lesson.setId(UUID.randomUUID());

        lessonTranslationService.saveTranslations(List.of(moduleDto), List.of(lesson));

        verify(lessonTranslationRepository).saveAll(anyList());
    }

    @Test
    void saveTranslations_WithEmptyTranslations_ShouldNotSave() {
        ModuleCreateRequestDto moduleDto = new ModuleCreateRequestDto();
        LessonCreateRequestDto lessonDto = new LessonCreateRequestDto();
        lessonDto.setTranslations(Collections.emptyList());
        moduleDto.setLessons(List.of(lessonDto));

        Lesson lesson = new Lesson();
        lesson.setId(UUID.randomUUID());

        lessonTranslationService.saveTranslations(List.of(moduleDto), List.of(lesson));

        verify(lessonTranslationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void saveTranslations_WithNullTranslations_ShouldNotSave() {
        ModuleCreateRequestDto moduleDto = new ModuleCreateRequestDto();
        LessonCreateRequestDto lessonDto = new LessonCreateRequestDto();
        lessonDto.setTranslations(null);
        moduleDto.setLessons(List.of(lessonDto));

        Lesson lesson = new Lesson();
        lesson.setId(UUID.randomUUID());

        lessonTranslationService.saveTranslations(List.of(moduleDto), List.of(lesson));

        verify(lessonTranslationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void findByLessonId_WithValidId_ShouldReturnTranslations() {
        UUID lessonId = UUID.randomUUID();
        LessonTranslation translation = new LessonTranslation();
        translation.setId(UUID.randomUUID());

        when(lessonTranslationRepository.findLessonTranslationById(lessonId)).thenReturn(List.of(translation));

        List<LessonTranslation> result = lessonTranslationService.findByLessonId(lessonId);

        assertEquals(1, result.size());
        verify(lessonTranslationRepository).findLessonTranslationById(lessonId);
    }

    @Test
    void findByLessonId_WithNonExistingId_ShouldReturnEmptyList() {
        UUID nonExistingId = UUID.randomUUID();

        when(lessonTranslationRepository.findLessonTranslationById(nonExistingId)).thenReturn(Collections.emptyList());

        List<LessonTranslation> result = lessonTranslationService.findByLessonId(nonExistingId);

        assertTrue(result.isEmpty());
        verify(lessonTranslationRepository).findLessonTranslationById(nonExistingId);
    }
}