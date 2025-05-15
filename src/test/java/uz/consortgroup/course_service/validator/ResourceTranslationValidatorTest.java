package uz.consortgroup.course_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.course.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.course.request.lesson.LessonCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.resource.ResourceCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ResourceTranslationValidatorTest {

    @InjectMocks
    private ResourceTranslationValidator validator;

    @Test
    void testValidateResourcesValidInput() {
        LessonCreateRequestDto lessonDto = new LessonCreateRequestDto();
        ResourceCreateRequestDto resourceDto = new ResourceCreateRequestDto();
        lessonDto.setResources(List.of(resourceDto));
        Resource resource = new Resource();
        List<Resource> resources = List.of(resource);
        List<ResourceCreateRequestDto> result = validator.validateResources(lessonDto, resources)
                .collect(Collectors.toList());
        assertEquals(1, result.size());
        assertEquals(resourceDto, result.get(0));
    }

    @Test
    void testValidateResourcesEmptyLessonResources() {
        LessonCreateRequestDto lessonDto = new LessonCreateRequestDto();
        lessonDto.setResources(Collections.emptyList());
        Resource resource = new Resource();
        List<Resource> resources = List.of(resource);
        List<ResourceCreateRequestDto> result = validator.validateResources(lessonDto, resources)
                .collect(Collectors.toList());
        assertTrue(result.isEmpty());
    }

    @Test
    void testValidateResourcesNullLessonResources() {
        LessonCreateRequestDto lessonDto = new LessonCreateRequestDto();
        lessonDto.setResources(null);
        Resource resource = new Resource();
        List<Resource> resources = List.of(resource);
        List<ResourceCreateRequestDto> result = validator.validateResources(lessonDto, resources)
                .collect(Collectors.toList());
        assertTrue(result.isEmpty());
    }

    @Test
    void testValidateResourcesEmptyResources() {
        LessonCreateRequestDto lessonDto = new LessonCreateRequestDto();
        ResourceCreateRequestDto resourceDto = new ResourceCreateRequestDto();
        lessonDto.setResources(List.of(resourceDto));
        List<Resource> resources = Collections.emptyList();
        List<ResourceCreateRequestDto> result = validator.validateResources(lessonDto, resources)
                .collect(Collectors.toList());
        assertTrue(result.isEmpty());
    }

    @Test
    void testValidateTranslationsFromResourceDtoValidInput() {
        ResourceCreateRequestDto resourceDto = new ResourceCreateRequestDto();
        ResourceTranslationRequestDto translationDto = new ResourceTranslationRequestDto();
        translationDto.setLanguage(Language.ENGLISH);
        translationDto.setTitle("Title");
        translationDto.setDescription("Description");
        resourceDto.setTranslations(List.of(translationDto));
        Resource resource = new Resource();
        List<ResourceTranslation> result = validator.validateTranslations(resourceDto, resource)
                .collect(Collectors.toList());
        assertEquals(1, result.size());
        ResourceTranslation translation = result.get(0);
        assertEquals(resource, translation.getResource());
        assertEquals(Language.ENGLISH, translation.getLanguage());
        assertEquals("Title", translation.getTitle());
        assertEquals("Description", translation.getDescription());
    }

    @Test
    void testValidateTranslationsFromResourceDtoNullTranslations() {
        ResourceCreateRequestDto resourceDto = new ResourceCreateRequestDto();
        resourceDto.setTranslations(null);
        Resource resource = new Resource();
        List<ResourceTranslation> result = validator.validateTranslations(resourceDto, resource)
                .collect(Collectors.toList());
        assertTrue(result.isEmpty());
    }

    @Test
    void testValidateTranslationsFromDtoListValidInput() {
        ResourceTranslationRequestDto translationDto = new ResourceTranslationRequestDto();
        translationDto.setLanguage(Language.ENGLISH);
        translationDto.setTitle("Title");
        translationDto.setDescription("Description");
        List<ResourceTranslationRequestDto> dtoList = List.of(translationDto);
        Resource resource = new Resource();
        List<ResourceTranslation> result = validator.validateTranslations(dtoList, resource)
                .collect(Collectors.toList());
        assertEquals(1, result.size());
        ResourceTranslation translation = result.get(0);
        assertEquals(resource, translation.getResource());
        assertEquals(Language.ENGLISH, translation.getLanguage());
        assertEquals("Title", translation.getTitle());
        assertEquals("Description", translation.getDescription());
    }

    @Test
    void testValidateTranslationsFromDtoListEmptyList() {
        List<ResourceTranslationRequestDto> dtoList = Collections.emptyList();
        Resource resource = new Resource();
        List<ResourceTranslation> result = validator.validateTranslations(dtoList, resource)
                .collect(Collectors.toList());
        assertTrue(result.isEmpty());
        assertTrue(resource.getTranslations().isEmpty());
    }

    @Test
    void testValidateTranslationsFromDtoListNullList() {
        List<ResourceTranslationRequestDto> dtoList = null;
        Resource resource = new Resource();
        List<ResourceTranslation> result = validator.validateTranslations(dtoList, resource)
                .collect(Collectors.toList());
        assertTrue(result.isEmpty());
        assertTrue(resource.getTranslations().isEmpty());
    }
}