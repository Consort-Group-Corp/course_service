package uz.consortgroup.course_service.service.resourse;

import uz.consortgroup.course_service.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.dto.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;

import java.util.List;
import java.util.UUID;

public interface ResourceTranslationService {
    void saveTranslations(List<ModuleCreateRequestDto> modules, List<Resource> savedResources);
    void saveTranslations(List<ResourceTranslationRequestDto> translationDtos, Resource resource);
    void saveAllTranslations(List<ResourceTranslation> translations);
    List<ResourceTranslation> findResourceTranslationById(UUID id);
}
