package uz.consortgroup.course_service.service.lesson.translation;

import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.LessonTranslation;

import java.util.List;
import java.util.UUID;

public interface LessonTranslationService {
    void saveTranslations(List<ModuleCreateRequestDto> lessonDtos, List<Lesson> lessons);
    List<LessonTranslation> findByLessonId(UUID lessonId);
}
