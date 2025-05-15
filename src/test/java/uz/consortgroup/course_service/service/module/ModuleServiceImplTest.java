package uz.consortgroup.course_service.service.module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.repository.ModuleRepository;
import uz.consortgroup.course_service.validator.CourseValidator;
import uz.consortgroup.course_service.validator.ModuleValidator;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuleServiceImplTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private CourseValidator courseValidator;

    @Mock
    private ModuleValidator moduleValidator;

    @InjectMocks
    private ModuleServiceImpl moduleService;

    private Course course;
    private ModuleCreateRequestDto dto1;
    private ModuleCreateRequestDto dto2;

    @BeforeEach
    void setUp() {
        course = Course.builder().id(UUID.randomUUID()).build();
        dto1 = ModuleCreateRequestDto.builder()
                .moduleName("Mod1")
                .orderPosition(1)
                .isActive(true)
                .build();
        dto2 = ModuleCreateRequestDto.builder()
                .moduleName("Mod2")
                .orderPosition(2)
                .isActive(true)
                .build();
    }

    @Test
    void saveModules_Success() {
        List<ModuleCreateRequestDto> dtos = List.of(dto1, dto2);
        List<Module> saved = List.of(
                Module.builder().moduleName("Mod1").orderPosition(1).isActive(true).course(course).build(),
                Module.builder().moduleName("Mod2").orderPosition(2).isActive(true).course(course).build()
        );

        when(moduleRepository.saveAll(anyList())).thenReturn(saved);

        List<Module> result = moduleService.saveModules(dtos, course);

        assertEquals(2, result.size());
        assertEquals("Mod1", result.get(0).getModuleName());
        assertEquals(2, result.get(1).getOrderPosition());
        verify(courseValidator).validateCourse(course);
        verify(moduleValidator).validateModules(dtos);
        verify(moduleRepository).saveAll(anyList());
    }

    @Test
    void saveModules_WhenCourseNotValidated_ShouldThrow() {
        doThrow(new IllegalArgumentException("No course id")).when(courseValidator).validateCourse(course);

        List<ModuleCreateRequestDto> dtos = List.of(dto1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                moduleService.saveModules(dtos, course)
        );
        assertEquals("No course id", ex.getMessage());
        verify(courseValidator).validateCourse(course);
        verifyNoInteractions(moduleRepository);
    }

    @Test
    void saveModules_WhenDtoInvalid_ShouldThrow() {
        doNothing().when(courseValidator).validateCourse(course);
        doThrow(new IllegalArgumentException("Bad modules")).when(moduleValidator).validateModules(anyList());

        List<ModuleCreateRequestDto> dtos = List.of(dto1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                moduleService.saveModules(dtos, course)
        );
        assertEquals("Bad modules", ex.getMessage());
        verify(courseValidator).validateCourse(course);
        verify(moduleValidator).validateModules(dtos);
        verifyNoInteractions(moduleRepository);
    }
}
