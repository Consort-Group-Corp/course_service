package uz.consortgroup.course_service.service.lesson.translation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.lesson.LessonCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.LessonTranslation;
import uz.consortgroup.course_service.repository.LessonTranslationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class LessonTranslationServiceImpl implements LessonTranslationService {
    private final LessonTranslationRepository lessonTranslationRepository;

    @Override
    @Transactional
    public void saveTranslations(List<ModuleCreateRequestDto> moduleDtos, List<Lesson> savedLessons) {
        log.info("Saving lesson translations for {} modules", moduleDtos.size());
        List<LessonTranslation> translations = new ArrayList<>();
        int lessonIndex = 0;

        for (ModuleCreateRequestDto moduleDto : moduleDtos) {
            for (LessonCreateRequestDto lessonDto : moduleDto.getLessons()) {
                Lesson lesson = savedLessons.get(lessonIndex++);
                List<LessonTranslation> lessonTranslations = lessonDto.getTranslations() != null
                        ? lessonDto.getTranslations().stream()
                        .map(t -> LessonTranslation.builder()
                                .lesson(lesson)
                                .language(t.getLanguage())
                                .title(t.getTitle())
                                .description(t.getDescription())
                                .build())
                        .toList()
                        : List.of();

                translations.addAll(lessonTranslations);
                lesson.setTranslations(lessonTranslations);
            }
        }

        lessonTranslationRepository.saveAll(translations);
        log.info("Saved {} lesson translations", translations.size());
    }

    @Override
    public List<LessonTranslation> findByLessonId(UUID lessonId) {
        log.info("Fetching translations for lessonId={}", lessonId);
        return lessonTranslationRepository.findLessonTranslationById(lessonId);
    }
}
