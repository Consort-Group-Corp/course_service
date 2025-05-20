package uz.consortgroup.course_service.service.course.translation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.course.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseTranslationRequestDto;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.entity.CourseTranslation;
import uz.consortgroup.course_service.repository.CourseTranslationRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourseTranslationServiceImplTest {
    @Mock
    private CourseTranslationRepository courseTranslationRepo;

    @InjectMocks
    private CourseTranslationServiceImpl courseTranslationService;

    @Test
    void saveTranslations_ShouldReturnSavedTranslations() {
        Course course = new Course();
        course.setId(UUID.randomUUID());

        CourseTranslationRequestDto dto1 = new CourseTranslationRequestDto(Language.ENGLISH, "Title", "Description", "slug");
        CourseTranslationRequestDto dto2 = new CourseTranslationRequestDto(Language.RUSSIAN, "Заголовок", "Описание", "slug-ru");
        List<CourseTranslationRequestDto> requestDtos = List.of(dto1, dto2);

        when(courseTranslationRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<CourseTranslation> result = courseTranslationService.saveTranslations(requestDtos, course);

        assertEquals(2, result.size());
        assertEquals(Language.ENGLISH, result.get(0).getLanguage());
        assertEquals(Language.RUSSIAN, result.get(1).getLanguage());
        verify(courseTranslationRepo, times(1)).saveAll(anyList());
    }

    @Test
    void saveTranslations_WithEmptyList_ShouldReturnEmptyList() {
        Course course = new Course();
        List<CourseTranslationRequestDto> emptyList = List.of();

        when(courseTranslationRepo.saveAll(anyList())).thenReturn(List.of());

        List<CourseTranslation> result = courseTranslationService.saveTranslations(emptyList, course);

        assertTrue(result.isEmpty());
        verify(courseTranslationRepo, times(1)).saveAll(anyList());
    }

    @Test
    void findByCourseId_ShouldReturnTranslations() {
        UUID courseId = UUID.randomUUID();
        CourseTranslation translation1 = new CourseTranslation();
        CourseTranslation translation2 = new CourseTranslation();
        List<CourseTranslation> expected = List.of(translation1, translation2);

        when(courseTranslationRepo.findByCourseId(courseId)).thenReturn(expected);

        List<CourseTranslation> result = courseTranslationService.findByCourseId(courseId);

        assertEquals(2, result.size());
        verify(courseTranslationRepo, times(1)).findByCourseId(courseId);
    }

    @Test
    void findByCourseId_WithNonExistingId_ShouldReturnEmptyList() {
        UUID nonExistingId = UUID.randomUUID();

        when(courseTranslationRepo.findByCourseId(nonExistingId)).thenReturn(List.of());

        List<CourseTranslation> result = courseTranslationService.findByCourseId(nonExistingId);

        assertTrue(result.isEmpty());
        verify(courseTranslationRepo, times(1)).findByCourseId(nonExistingId);
    }
}
