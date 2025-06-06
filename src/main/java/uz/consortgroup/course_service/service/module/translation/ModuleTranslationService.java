package uz.consortgroup.course_service.service.module.translation;

import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.entity.ModuleTranslation;

import java.util.List;
import java.util.UUID;

public interface ModuleTranslationService {
    void saveTranslations(List<ModuleCreateRequestDto> modulesDto, List<Module> savedModules);
    List<ModuleTranslation> findByModuleId(UUID moduleId);
}
