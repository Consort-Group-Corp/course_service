package uz.consortgroup.course_service.service.course.translation;

import uz.consortgroup.core.api.v1.dto.course.request.course.CourseTranslationRequestDto;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.entity.CourseTranslation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseTranslationService {
    List<CourseTranslation> saveTranslations(List<CourseTranslationRequestDto> translations, Course course);
    List<CourseTranslation> findByCourseId(UUID courseId);
    Optional<CourseTranslation> findFirstTranslation(UUID courseId);
}