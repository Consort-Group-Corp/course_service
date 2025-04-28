package uz.consortgroup.course_service.service.lesson;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.course_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.course_service.asspect.annotation.LoggingAspectBeforeMethod;
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
    public void saveTranslations(List<ModuleCreateRequestDto> lessonDtos, List<Lesson> lessons) {
        List<LessonTranslation> translations = new ArrayList<>();

        for (int i = 0; i < lessons.size(); i++) {
            Lesson lesson = lessons.get(i);
            ModuleCreateRequestDto dto = lessonDtos.get(i);

            dto.getTranslations().forEach(t -> {
                translations.add(LessonTranslation.builder()
                        .lesson(lesson)
                        .language(t.getLanguage())
                        .title(t.getTitle())
                        .description(t.getDescription())
                        .build());
            });
        }

        lessonTranslationRepository.saveAll(translations);
    }

    @Override
    public List<LessonTranslation> findByLessonId(UUID lessonId) {
        return lessonTranslationRepository.findLessonTranslationById((lessonId));
    }
}
