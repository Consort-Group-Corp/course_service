package uz.consortgroup.course_service.validator;

import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.request.module.ModuleCreateRequestDto;

import java.util.List;

@Component
public class ModuleValidator {
    public void validateModules(List<ModuleCreateRequestDto> modulesDto) {
        if (modulesDto == null || modulesDto.isEmpty()) {
            return;
        }

        for (ModuleCreateRequestDto dto : modulesDto) {
            if (dto.getModuleName() == null || dto.getOrderPosition() == null) {
                throw new IllegalArgumentException("Module name and order position must not be null for module: " + dto);
            }
        }
    }
}
