package uz.consortgroup.course_service.service.lesson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.exception.LessonNotFoundException;
import uz.consortgroup.course_service.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;

    @Override
    @Transactional
    public List<Lesson> saveLessons(List<ModuleCreateRequestDto> moduleDtos, List<Module> modules) {
        log.info("Saving lessons for {} modules", moduleDtos.size());
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

        List<Lesson> saved = lessonRepository.saveAll(allLessons);
        log.info("Saved {} lessons", saved.size());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lesson> findByModuleIds(List<UUID> moduleIds) {
        if (moduleIds == null || moduleIds.isEmpty()) {
            log.info("No module IDs provided, returning empty lesson list");
            return List.of();
        }
        log.info("Fetching lessons for moduleIds: {}", moduleIds);
        return lessonRepository.findLessonsByModuleIds(moduleIds);
    }

    @Override
    public List<Lesson> findByModuleId(UUID moduleId) {
        log.info("Fetching lessons for moduleId={}", moduleId);
        return lessonRepository.findAllByModuleId(moduleId);
    }

    @Override
    public Lesson getLessonEntity(UUID lessonId) {
        log.info("Fetching lesson by id={}", lessonId);
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found"));
    }

    @Override
    public UUID findLessonId(UUID lessonId) {
        log.info("Resolving lesson ID for lessonId={}", lessonId);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found"));
        return lesson.getId();
    }
}
