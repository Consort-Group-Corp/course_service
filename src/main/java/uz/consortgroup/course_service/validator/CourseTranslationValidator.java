package uz.consortgroup.course_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseTranslationRequestDto;
import uz.consortgroup.course_service.exception.ConflictException;
import uz.consortgroup.course_service.exception.SlugAlreadyExistsException;
import uz.consortgroup.course_service.repository.CourseTranslationRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseTranslationValidator {

    private final CourseTranslationRepository translationRepository;

    public void validateSlugsOrThrow(List<CourseTranslationRequestDto> dtos) {
        List<String> requested = dtos.stream()
                .map(d -> normalize(d.getSlug()))
                .toList();

        Set<String> dupInPayload = requested.stream()
                .collect(java.util.stream.Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());

        if (!dupInPayload.isEmpty()) {
            log.error("Duplicate slugs in payload: {}", dupInPayload);
            throw new ConflictException("Slug already exists");
        }

        Set<String> requestedSet = new HashSet<>(requested);
        Set<String> taken = translationRepository.findExistingSlugs(requestedSet);
        if (!taken.isEmpty()) {
            log.error("Slug already exists: {}", taken);
            throw new SlugAlreadyExistsException("Slug already exists: " + taken);
        }
    }

    private String normalize(String slug) {
        return slug == null ? null : slug.trim().toLowerCase();
    }
}

