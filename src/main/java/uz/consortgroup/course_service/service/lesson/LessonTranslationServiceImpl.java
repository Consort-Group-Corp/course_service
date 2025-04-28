package uz.consortgroup.course_service.service.lesson;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.course_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.course_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.course_service.dto.request.lesson.LessonCreateRequestDto;
import uz.consortgroup.course_service.dto.request.lesson.LessonTranslationRequestDto;
import uz.consortgroup.course_service.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.LessonTranslation;
import uz.consortgroup.course_service.repository.LessonTranslationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LessonTranslationServiceImpl implements LessonTranslationService {
    private final LessonTranslationRepository lessonTranslationRepository;

    @Override
    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void saveTranslations(List<ModuleCreateRequestDto> moduleDtos, List<Lesson> savedLessons) {
        List<LessonTranslation> translations = new ArrayList<>();
        int lessonIndex = 0;

        for (ModuleCreateRequestDto moduleDto : moduleDtos) {
            for (LessonCreateRequestDto lessonDto : moduleDto.getLessons()) {
                Lesson lesson = savedLessons.get(lessonIndex++);
                if (lessonDto.getTranslations() != null) {
                    for (LessonTranslationRequestDto translationDto : lessonDto.getTranslations()) {
                        translations.add(
                                LessonTranslation.builder()
                                        .lesson(lesson)
                                        .language(translationDto.getLanguage())
                                        .title(translationDto.getTitle())
                                        .description(translationDto.getDescription())
                                        .build()
                        );
                    }
                }
            }
        }

        translations = lessonTranslationRepository.saveAll(translations);

        lessonIndex = 0;
        for (ModuleCreateRequestDto moduleDto : moduleDtos) {
            for (LessonCreateRequestDto lessonDto : moduleDto.getLessons()) {
                Lesson lesson = savedLessons.get(lessonIndex++);
                List<LessonTranslation> lessonTranslations = translations.stream()
                        .filter(t -> t.getLesson().equals(lesson))
                        .toList();
                lesson.setTranslations(lessonTranslations);
            }
        }
    }

    @Override
    public List<LessonTranslation> findByLessonId(UUID lessonId) {
        return lessonTranslationRepository.findLessonTranslationById((lessonId));
    }
}
