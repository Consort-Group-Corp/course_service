package uz.consortgroup.course_service.service.resourse.translation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.request.lesson.LessonCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.request.resource.ResourceCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.repository.ResourceTranslationRepository;
import uz.consortgroup.course_service.validator.ResourceTranslationValidator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceTranslationServiceImplTest {

    @Mock
    private ResourceTranslationRepository resourceTranslationRepository;

    @Mock
    private ResourceTranslationValidator validator;

    @InjectMocks
    private ResourceTranslationServiceImpl resourceTranslationService;

    @Test
    void saveTranslations_WithModuleAndResources_ShouldSaveTranslations() {
        ModuleCreateRequestDto moduleDto = new ModuleCreateRequestDto();
        LessonCreateRequestDto lessonDto = new LessonCreateRequestDto();
        UUID lessonId = UUID.randomUUID();
        lessonDto.setLessonId(lessonId);
        ResourceCreateRequestDto resourceDto = new ResourceCreateRequestDto();
        moduleDto.setLessons(List.of(lessonDto));

        Resource resource = new Resource();
        ResourceTranslation translation = new ResourceTranslation();

        when(validator.validateResources(any(LessonCreateRequestDto.class), anyList()))
                .thenReturn(List.of(resourceDto).stream());
        when(validator.validateTranslations(any(ResourceCreateRequestDto.class), eq(resource)))
                .thenReturn(List.of(translation).stream());

        resourceTranslationService.saveTranslations(
                List.of(moduleDto),
                Map.of(lessonId, List.of(resource))
        );

        verify(resourceTranslationRepository).saveAll(anyList());
    }

    @Test
    void saveTranslations_WithEmptyModules_ShouldNotSave() {
        resourceTranslationService.saveTranslations(
            Collections.emptyList(),
            Map.of(UUID.randomUUID(), List.of(new Resource()))
        );

        verify(resourceTranslationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void saveTranslations_WithResourceAndTranslations_ShouldSaveTranslations() {
        ResourceTranslationRequestDto translationDto = new ResourceTranslationRequestDto();
        Resource resource = new Resource();
        ResourceTranslation translation = new ResourceTranslation();
        
        when(validator.validateTranslations(anyList(), any())).thenReturn(List.of(translation).stream());
        when(resourceTranslationRepository.saveAll(anyList())).thenReturn(List.of(translation));

        resourceTranslationService.saveTranslations(
            List.of(translationDto),
            resource
        );

        verify(resourceTranslationRepository).saveAll(anyList());
        assertNotNull(resource.getTranslations());
    }

    @Test
    void saveTranslations_WithEmptyTranslations_ShouldNotSave() {
        Resource resource = new Resource();
        resourceTranslationService.saveTranslations(
            Collections.emptyList(),
            resource
        );

        verify(resourceTranslationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void saveAllTranslations_ShouldReturnSavedTranslations() {
        ResourceTranslation translation = new ResourceTranslation();
        when(resourceTranslationRepository.saveAll(anyList())).thenReturn(List.of(translation));

        List<ResourceTranslation> result = resourceTranslationService.saveAllTranslations(
            List.of(translation)
        );

        assertEquals(1, result.size());
    }

    @Test
    void findResourceTranslationById_ShouldReturnTranslations() {
        UUID resourceId = UUID.randomUUID();
        ResourceTranslation translation = new ResourceTranslation();
        when(resourceTranslationRepository.findResourceTranslationById(resourceId))
            .thenReturn(List.of(translation));

        List<ResourceTranslation> result = resourceTranslationService.findResourceTranslationById(resourceId);

        assertEquals(1, result.size());
    }
}