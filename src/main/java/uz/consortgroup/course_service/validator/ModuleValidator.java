package uz.consortgroup.course_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;

import java.util.List;

@Component
@Slf4j
public class ModuleValidator {

    public void validateModules(List<ModuleCreateRequestDto> modulesDto) {
        if (modulesDto == null || modulesDto.isEmpty()) {
            log.warn("Modules DTO list is null or empty");
            return;
        }

        for (ModuleCreateRequestDto dto : modulesDto) {
            if (dto.getModuleName() == null || dto.getOrderPosition() == null) {
                log.error("Invalid module data: {}", dto);
                throw new IllegalArgumentException("Module name and order position must not be null for module: " + dto);
            }
        }

        log.debug("All modules passed validation. Total modules: {}", modulesDto.size());
    }
}
