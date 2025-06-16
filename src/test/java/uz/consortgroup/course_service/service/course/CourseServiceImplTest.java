package uz.consortgroup.course_service.service.course;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.course.enumeration.CourseStatus;
import uz.consortgroup.core.api.v1.dto.course.enumeration.CourseType;
import uz.consortgroup.core.api.v1.dto.course.enumeration.PriceType;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CoursePurchaseValidationResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CourseResponseDto;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.exception.CourseNotFoundException;
import uz.consortgroup.course_service.mapper.CourseMapper;
import uz.consortgroup.course_service.mapper.CourseTranslationMapper;
import uz.consortgroup.course_service.mapper.ModuleMapper;
import uz.consortgroup.course_service.mapper.ModuleTranslationMapper;
import uz.consortgroup.course_service.repository.CourseRepository;
import uz.consortgroup.course_service.service.course.translation.CourseTranslationService;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.lesson.translation.LessonTranslationService;
import uz.consortgroup.course_service.service.module.ModuleService;
import uz.consortgroup.course_service.service.module.translation.ModuleTranslationService;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseTranslationService courseTranslationService;

    @Mock
    private ModuleService moduleService;

    @Mock
    private ModuleTranslationService moduleTranslationService;

    @Mock
    private LessonService lessonService;

    @Mock
    private LessonTranslationService lessonTranslationService;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private CourseTranslationMapper courseTranslationMapper;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ModuleMapper moduleMapper;

    @Mock
    private ModuleTranslationMapper moduleTranslationMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    @BeforeEach
    void setUp() {
        try {
            Field field = CourseServiceImpl.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(courseService, entityManager);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject EntityManager", e);
        }
    }

    @Test
    void create_WithValidData_ShouldReturnCourseResponse() {
        CourseCreateRequestDto requestDto = new CourseCreateRequestDto();
        requestDto.setTranslations(Collections.emptyList());
        requestDto.setModules(Collections.emptyList());

        Course course = new Course();
        UUID courseId = UUID.randomUUID();
        course.setId(courseId);

        CourseResponseDto expectedResponse = new CourseResponseDto();
        expectedResponse.setId(courseId);

        when(courseMapper.toEntity(requestDto)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        when(entityManager.getReference(Course.class, courseId)).thenReturn(course);
        when(courseTranslationService.findByCourseId(courseId)).thenReturn(Collections.emptyList());
        when(courseMapper.toResponseDto(course)).thenReturn(expectedResponse);

        CourseResponseDto result = courseService.create(requestDto);

        assertNotNull(result);
        assertEquals(courseId, result.getId());
        verify(courseRepository).save(course);
        verify(courseTranslationService).saveTranslations(any(), any());
        verify(entityManager).getReference(Course.class, courseId);
    }

    @Test
    void create_WhenEntityManagerFails_ShouldThrowException() {
        CourseCreateRequestDto requestDto = new CourseCreateRequestDto();
        requestDto.setTranslations(Collections.emptyList());
        requestDto.setModules(Collections.emptyList());

        Course course = new Course();
        UUID courseId = UUID.randomUUID();
        course.setId(courseId);

        when(courseMapper.toEntity(requestDto)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        when(entityManager.getReference(Course.class, courseId))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> courseService.create(requestDto));
    }

    @Test
    void create_WithEmptyModules_ShouldHandleGracefully() {
        CourseCreateRequestDto requestDto = new CourseCreateRequestDto();
        requestDto.setTranslations(Collections.emptyList());
        requestDto.setModules(Collections.emptyList());

        Course course = new Course();
        UUID courseId = UUID.randomUUID();
        course.setId(courseId);

        when(courseMapper.toEntity(requestDto)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        when(entityManager.getReference(Course.class, courseId)).thenReturn(course);
        when(courseTranslationService.findByCourseId(courseId)).thenReturn(Collections.emptyList());
        when(courseMapper.toResponseDto(course)).thenReturn(new CourseResponseDto());

        when(moduleService.saveModules(Collections.emptyList(), course)).thenReturn(Collections.emptyList());

        CourseResponseDto result = courseService.create(requestDto);

        assertNotNull(result);
        verify(moduleService).saveModules(Collections.emptyList(), course);
    }

    @Test
    void create_WhenCourseSaveFails_ShouldThrowException() {
        CourseCreateRequestDto requestDto = new CourseCreateRequestDto();
        requestDto.setTranslations(Collections.emptyList());
        requestDto.setModules(Collections.emptyList());

        when(courseMapper.toEntity(requestDto)).thenReturn(new Course());
        when(courseRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> courseService.create(requestDto));
        verify(courseTranslationService, never()).saveTranslations(any(), any());
    }

    @Test
    void validateCourseForPurchase_ShouldReturnValidResponse() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        course.setId(courseId);
        course.setCourseStatus(CourseStatus.ACTIVE);
        course.setCourseType(CourseType.PREMIUM);
        course.setPriceType(PriceType.PAID);
        course.setPriceAmount(BigDecimal.valueOf(100));
        course.setStartTime(Instant.now());
        course.setEndTime(Instant.now().plusSeconds(3600));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        CoursePurchaseValidationResponseDto response = courseService.validateCourseForPurchase(courseId);

        assertNotNull(response);
        assertEquals(courseId, response.getId());
        assertEquals(CourseStatus.ACTIVE, response.getCourseStatus());
        assertTrue(response.isPurchasable());
    }

    @Test
    void validateCourseForPurchase_ShouldThrowNotFoundException() {
        UUID courseId = UUID.randomUUID();

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class, () ->
                courseService.validateCourseForPurchase(courseId));
    }

    @Test
    void delete_ShouldDeleteExistingCourse() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        course.setId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertDoesNotThrow(() -> courseService.delete(courseId));
        verify(courseRepository).delete(course);
    }

    @Test
    void delete_ShouldDoNothingWhenCourseNotFound() {
        UUID courseId = UUID.randomUUID();

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> courseService.delete(courseId));
        verify(courseRepository, never()).delete(any());
    }

    @Test
    void getCourseById_ShouldReturnCourseResponse() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        course.setId(courseId);
        CourseResponseDto expectedResponse = new CourseResponseDto();
        expectedResponse.setId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMapper.toResponseDto(course)).thenReturn(expectedResponse);

        CourseResponseDto response = courseService.getCourseById(courseId);

        assertNotNull(response);
        assertEquals(courseId, response.getId());
    }

    @Test
    void getCourseById_ShouldThrowNotFoundException() {
        UUID courseId = UUID.randomUUID();

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class, () ->
                courseService.getCourseById(courseId));
    }

}