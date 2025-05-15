package uz.consortgroup.course_service.service.resourse.translation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.course_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.repository.ResourceTranslationRepository;
import uz.consortgroup.course_service.validator.ResourceTranslationValidator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceTranslationServiceImpl implements ResourceTranslationService {
    private final ResourceTranslationRepository resourceTranslationRepository;
    private final ResourceTranslationValidator validator;

    @Override
    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void saveTranslations(List<ModuleCreateRequestDto> modules, Map<UUID, List<Resource>> lessonResourcesMap) {
        List<ResourceTranslation> translations = modules.stream()
                .flatMap(moduleDto -> moduleDto.getLessons().stream())
                .flatMap(lessonDto -> {
                    List<Resource> resources = lessonResourcesMap.getOrDefault(lessonDto.getLessonId(), List.of());
                    Iterator<Resource> resourceIterator = resources.iterator();
                    return validator.validateResources(lessonDto, resources)
                            .filter(resourceDto -> resourceIterator.hasNext())
                            .flatMap(resourceDto -> validator.validateTranslations(resourceDto, resourceIterator.next()));
                })
                .toList();

        resourceTranslationRepository.saveAll(translations);
    }

    @Override
    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void saveTranslations(List<ResourceTranslationRequestDto> dtoList, Resource resource) {
        List<ResourceTranslation> translations = validator.validateTranslations(dtoList, resource)
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