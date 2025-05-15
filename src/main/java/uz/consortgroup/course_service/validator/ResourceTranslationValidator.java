package uz.consortgroup.course_service.validator;

import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.request.lesson.LessonCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.request.resource.ResourceCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class ResourceTranslationValidator {

    public Stream<ResourceCreateRequestDto> validateResources(LessonCreateRequestDto lessonDto, List<Resource> resources) {
        return (lessonDto.getResources() == null || lessonDto.getResources().isEmpty()
                || resources.isEmpty()) ? Stream.empty() : lessonDto.getResources().stream();
    }

    public Stream<ResourceTranslation> validateTranslations(ResourceCreateRequestDto resourceDto, Resource resource) {
        if (resourceDto.getTranslations() == null) {
            return Stream.empty();
        }

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
