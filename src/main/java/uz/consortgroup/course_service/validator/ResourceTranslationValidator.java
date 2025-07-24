package uz.consortgroup.course_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.course.request.lesson.LessonCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.resource.ResourceCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@Slf4j
public class ResourceTranslationValidator {

    public Stream<ResourceCreateRequestDto> validateResources(LessonCreateRequestDto lessonDto, List<Resource> resources) {
        if (lessonDto.getResources() == null || lessonDto.getResources().isEmpty() || resources.isEmpty()) {
            log.debug("No resources to validate for lesson: {}", lessonDto.getLessonId());
            return Stream.empty();
        }
        log.debug("Validating {} resources for lesson: {}", lessonDto.getResources().size(), lessonDto.getLessonId());
        return lessonDto.getResources().stream();
    }

    public Stream<ResourceTranslation> validateTranslations(ResourceCreateRequestDto resourceDto, Resource resource) {
        if (resourceDto.getTranslations() == null) {
            log.debug("No translations found for resource DTO: {}", resourceDto);
            return Stream.empty();
        }

        log.debug("Validating {} translations for resource: {}", resourceDto.getTranslations().size(), resource.getId());
        return resourceDto.getTranslations().stream()
                .map(translationDto -> ResourceTranslation.builder()
                        .resource(resource)
                        .language(translationDto.getLanguage())
                        .title(translationDto.getTitle())
                        .description(translationDto.getDescription())
                        .build());
    }

    public Stream<ResourceTranslation> validateTranslations(List<ResourceTranslationRequestDto> dtoList, Resource resource) {
        if (dtoList == null || dtoList.isEmpty()) {
            log.debug("No translation DTOs to validate for resource: {}", resource.getId());
            resource.setTranslations(new ArrayList<>());
            return Stream.empty();
        }

        return dtoList.stream()
                .map(t -> ResourceTranslation.builder()
                        .resource(resource)
                        .language(t.getLanguage())
                        .title(t.getTitle())
                        .description(t.getDescription())
                        .build());
    }
}
