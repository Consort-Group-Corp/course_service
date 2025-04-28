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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceTranslationServiceImpl implements ResourceTranslationService {
    private final ResourceTranslationRepository resourceTranslationRepository;

    @Override
    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void saveTranslations(List<ModuleCreateRequestDto> modules, List<Resource> savedResources) {
        List<ResourceTranslation> translations = new ArrayList<>();

        Map<String, Resource> resourceByUrl = savedResources.stream()
                .collect(Collectors.toMap(Resource::getFileUrl, r -> r));

        for (ModuleCreateRequestDto moduleDto : modules) {
            for (LessonCreateRequestDto lessonDto : moduleDto.getLessons()) {
                for (ResourceCreateRequestDto resourceDto : lessonDto.getResources()) {
                    Resource resource = resourceByUrl.get(resourceDto.getFileUrl());
                    if (resource == null) continue;

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
    public void saveAllTranslations(List<ResourceTranslation> translations) {
        resourceTranslationRepository.saveAll(translations);
    }

    @Override
    @AllAspect
    public List<ResourceTranslation> findResourceTranslationById(UUID id) {
        return resourceTranslationRepository.findResourceTranslationById(id);
    }
}
