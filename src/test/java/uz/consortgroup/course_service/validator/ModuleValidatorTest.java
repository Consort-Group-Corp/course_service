package uz.consortgroup.course_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.request.module.ModuleCreateRequestDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ModuleValidatorTest {

    @InjectMocks
    private ModuleValidator validator;

    @Test
    void testValidateValidModules() {
        ModuleCreateRequestDto dto = new ModuleCreateRequestDto();
        dto.setModuleName("Test Module");
        dto.setOrderPosition(1);
        List<ModuleCreateRequestDto> modules = List.of(dto);
        assertDoesNotThrow(() -> validator.validateModules(modules));
    }

    @Test
    void testValidateEmptyModules() {
        assertDoesNotThrow(() -> validator.validateModules(Collections.emptyList()));
    }

    @Test
    void testValidateNullModules() {
        assertDoesNotThrow(() -> validator.validateModules(null));
    }

    @Test
    void testValidateModuleWithNullName() {
        ModuleCreateRequestDto dto = new ModuleCreateRequestDto();
        dto.setOrderPosition(1);
        List<ModuleCreateRequestDto> modules = List.of(dto);
        assertThrows(IllegalArgumentException.class, () -> validator.validateModules(modules));
    }

    @Test
    void testValidateModuleWithNullOrderPosition() {
        ModuleCreateRequestDto dto = new ModuleCreateRequestDto();
        dto.setModuleName("Test Module");
        List<ModuleCreateRequestDto> modules = List.of(dto);
        assertThrows(IllegalArgumentException.class, () -> validator.validateModules(modules));
    }

    @Test
    void testValidateModuleWithAllNullFields() {
        ModuleCreateRequestDto dto = new ModuleCreateRequestDto();
        List<ModuleCreateRequestDto> modules = List.of(dto);
        assertThrows(IllegalArgumentException.class, () -> validator.validateModules(modules));
    }
}