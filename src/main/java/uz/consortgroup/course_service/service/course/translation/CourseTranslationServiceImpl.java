package uz.consortgroup.course_service.service.course.translation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseTranslationRequestDto;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.entity.CourseTranslation;
import uz.consortgroup.course_service.repository.CourseTranslationRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseTranslationServiceImpl implements CourseTranslationService {
    private final CourseTranslationRepository courseTranslationRepo;

    @Override
    @Transactional
    public List<CourseTranslation> saveTranslations(List<CourseTranslationRequestDto> translations, Course course) {
        log.info("Saving translations for course with ID: {}", course.getId());

        List<CourseTranslation> entities = translations.stream()
                .map(dto -> CourseTranslation.builder()
                        .course(course)
                        .language(dto.getLanguage())
                        .title(dto.getTitle())
                        .description(dto.getDescription())
                        .slug(dto.getSlug())
                        .build())
                .toList();

        List<CourseTranslation> saved = courseTranslationRepo.saveAll(entities);

        log.info("Saved {} translation(s) for course ID: {}", saved.size(), course.getId());
        return saved;
    }

    @Override
    public List<CourseTranslation> findByCourseId(UUID courseId) {
        log.info("Fetching translations for course ID: {}", courseId);
        List<CourseTranslation> translations = courseTranslationRepo.findByCourseId(courseId);
        log.info("Found {} translation(s) for course ID: {}", translations.size(), courseId);
        return translations;
    }

    @Override
    public Optional<CourseTranslation> findFirstTranslation(UUID courseId) {
        log.info("Fetching first available translation for course ID: {}", courseId);
        Optional<CourseTranslation> translation = courseTranslationRepo.findFirstByCourseIdOrderByLanguageAsc(courseId);
        if (translation.isPresent()) {
            log.info("First translation found for course ID: {} with language: {}", courseId, translation.get().getLanguage());
        } else {
            log.warn("No translations found for course ID: {}", courseId);
        }
        return translation;
    }
}
