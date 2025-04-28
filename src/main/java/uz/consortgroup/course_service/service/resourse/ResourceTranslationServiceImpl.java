package uz.consortgroup.course_service.service.resourse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.course_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.course_service.dto.request.lesson.LessonCreateRequestDto;
import uz.consortgroup.course_service.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.dto.request.resource.ResourceCreateRequestDto;
import uz.consortgroup.course_service.dto.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.repository.ResourceTranslationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceTranslationServiceImpl implements ResourceTranslationService {
    private final ResourceTranslationRepository resourceTranslationRepository;

    @Override
    @Transactional
    public void saveTranslations(List<ModuleCreateRequestDto> modules, List<Resource> savedResources) {
        List<ResourceTranslation> translations = new ArrayList<>();
        int resourceIndex = 0;

        for (ModuleCreateRequestDto moduleDto : modules) {
            for (LessonCreateRequestDto lessonDto : moduleDto.getLessons()) {
                if (lessonDto.getResources() != null && !lessonDto.getResources().isEmpty()) {
                    for (ResourceCreateRequestDto resourceDto : lessonDto.getResources()) {
                        Resource resource = savedResources.get(resourceIndex++);
                        if (resourceDto.getTranslations() != null) {
                            for (ResourceTranslationRequestDto translationDto : resourceDto.getTranslations()) {
                                translations.add(
                                        ResourceTranslation.builder()
                                                .resource(resource)
                                                .language(translationDto.getLanguage())
                                                .title(translationDto.getTitle())
                                                .description(translationDto.getDescription())
                                                .build()
                                );
                            }
                        }
                    }
                }
            }
        }

        resourceTranslationRepository.saveAll(translations);
    }

    @Override
    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void saveTranslations(List<ResourceTranslationRequestDto> dtoList, Resource resource) {
        if (dtoList == null || dtoList.isEmpty()) {
            resource.setTranslations(new ArrayList<>());
            return;
        }

        List<ResourceTranslation> translations = dtoList.stream()
                .map(t -> ResourceTranslation.builder()
                        .resource(resource)
                        .language(t.getLanguage())
                        .title(t.getTitle())
                        .description(t.getDescription())
                        .build())
                .toList();

        translations = resourceTranslationRepository.saveAll(translations);

        translations.forEach(translation -> translation.setResource(resource));
        resource.setTranslations(translations);
    }

    @Override
    @AllAspect
    public List<ResourceTranslation> saveAllTranslations(List<ResourceTranslation> translations) {
        return resourceTranslationRepository.saveAll(translations);
    }

    @Override
    @AllAspect
    public List<ResourceTranslation> findResourceTranslationById(UUID id) {
        return resourceTranslationRepository.findResourceTranslationById(id);
    }
}
