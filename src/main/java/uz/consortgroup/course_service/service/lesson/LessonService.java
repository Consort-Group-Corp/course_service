package uz.consortgroup.course_service.service.lesson;

import uz.consortgroup.course_service.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Module;

import java.util.List;
import java.util.UUID;

public interface LessonService {
    List<Lesson> saveLessons(List<ModuleCreateRequestDto> lessonDtos, List<Module> module);
    List<Lesson> findByModuleId(UUID moduleId);
    Lesson getLessonEntity(UUID lessonId);

}
