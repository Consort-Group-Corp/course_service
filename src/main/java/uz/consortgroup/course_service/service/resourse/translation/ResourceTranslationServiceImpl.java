package uz.consortgroup.course_service.service.resourse.translation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.resource.ResourceTranslationRequestDto;
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
@Slf4j
public class ResourceTranslationServiceImpl implements ResourceTranslationService {
    private final ResourceTranslationRepository resourceTranslationRepository;
    private final ResourceTranslationValidator validator;

    @Override
    @Transactional
    public void saveTranslations(List<ModuleCreateRequestDto> modules, Map<UUID, List<Resource>> lessonResourcesMap) {
        log.info("Saving translations for {} modules", modules.size());

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
        log.debug("Saved {} resource translations", translations.size());
    }

    @Override
    @Transactional
    public void saveTranslations(List<ResourceTranslationRequestDto> dtoList, Resource resource) {
        log.info("Saving {} translations for resourceId={}", dtoList.size(), resource.getId());

        List<ResourceTranslation> translations = validator.validateTranslations(dtoList, resource).toList();
        translations = resourceTranslationRepository.saveAll(translations);

        translations.forEach(translation -> translation.setResource(resource));
        resource.setTranslations(translations);

        log.debug("Saved translations for resourceId={}", resource.getId());
    }

    @Override
    public List<ResourceTranslation> saveAllTranslations(List<ResourceTranslation> translations) {
        log.info("Saving {} translations in bulk", translations.size());
        List<ResourceTranslation> saved = resourceTranslationRepository.saveAll(translations);
        log.debug("Saved {} translations in bulk", saved.size());
        return saved;
    }

    @Override
    public List<ResourceTranslation> findResourceTranslationById(UUID id) {
        log.info("Fetching translations for resourceId={}", id);
        List<ResourceTranslation> list = resourceTranslationRepository.findResourceTranslationById(id);
        log.debug("Found {} translations for resourceId={}", list.size(), id);
        return list;
    }
}
