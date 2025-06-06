package uz.consortgroup.course_service.service.resourse.translation;

import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ResourceTranslationService {
    void saveTranslations(List<ModuleCreateRequestDto> modules, Map<UUID, List<Resource>> lessonResourcesMap);
    void saveTranslations(List<ResourceTranslationRequestDto> dtoList, Resource resource);
    List<ResourceTranslation> saveAllTranslations(List<ResourceTranslation> translations);
    List<ResourceTranslation> findResourceTranslationById(UUID id);
}
