package uz.consortgroup.course_service.service.lesson;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.exception.LessonNotFoundException;
import uz.consortgroup.course_service.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;

    @Override
    @Transactional
    @AllAspect
    public List<Lesson> saveLessons(List<ModuleCreateRequestDto> moduleDtos, List<Module> modules) {
        List<Lesson> allLessons = new ArrayList<>();

        for (int i = 0; i < moduleDtos.size(); i++) {
            ModuleCreateRequestDto moduleDto = moduleDtos.get(i);
            Module module = modules.get(i);

            List<Lesson> lessons = moduleDto.getLessons().stream()
                    .map(lessonDto -> Lesson.builder()
                            .module(module)
                            .orderPosition(lessonDto.getOrderPosition())
                            .contentUrl(lessonDto.getContentUrl())
                            .lessonType(lessonDto.getLessonType())
                            .isPreview(lessonDto.getIsPreview())
                            .durationMinutes(lessonDto.getDurationMinutes())
                            .build())
                    .toList();

            allLessons.addAll(lessons);
        }

        return lessonRepository.saveAll(allLessons);
    }


    @Override
    @AllAspect
    public List<Lesson> findByModuleId(UUID moduleId) {
        return lessonRepository.findAllByModuleId(moduleId);
    }

    @Override
    @AllAspect
    public Lesson getLessonEntity(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found"));
    }
}