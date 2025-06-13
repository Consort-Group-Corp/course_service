package uz.consortgroup.course_service.service.course.translation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseTranslationRequestDto;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.entity.CourseTranslation;
import uz.consortgroup.course_service.repository.CourseTranslationRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseTranslationServiceImpl implements CourseTranslationService {
    private final CourseTranslationRepository courseTranslationRepo;

    @Override
    @Transactional
    @AllAspect
    public List<CourseTranslation> saveTranslations(List<CourseTranslationRequestDto> translations, Course course) {
        List<CourseTranslation> entities = translations.stream()
                .map(dto -> CourseTranslation.builder()
                        .course(course)
                        .language(dto.getLanguage())
                        .title(dto.getTitle())
                        .description(dto.getDescription())
                        .slug(dto.getSlug())
                        .build())
                .toList();

        return courseTranslationRepo.saveAll(entities);
    }

    @Override
    @AllAspect
    public List<CourseTranslation> findByCourseId(UUID courseId) {
        return courseTranslationRepo.findByCourseId(courseId);
    }

    @Override
    @AllAspect
    public Optional<CourseTranslation> findFirstTranslation(UUID courseId) {
        return courseTranslationRepo.findFirstByCourseIdOrderByLanguageAsc(courseId);
    }
}