package uz.consortgroup.course_service.service.module.translation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.request.module.ModuleTranslationRequestDto;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.entity.ModuleTranslation;
import uz.consortgroup.course_service.repository.ModuleTranslationRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ModuleTranslationServiceImplTest {

    @Mock
    private ModuleTranslationRepository translationRepository;

    @InjectMocks
    private ModuleTranslationServiceImpl moduleTranslationService;

    @Test
    void saveTranslations_WithValidData_ShouldSaveTranslations() {
        ModuleCreateRequestDto moduleDto = new ModuleCreateRequestDto();
        ModuleTranslationRequestDto translationDto = new ModuleTranslationRequestDto();
        translationDto.setLanguage(Language.ENGLISH);
        translationDto.setTitle("Title");
        translationDto.setDescription("Description");
        moduleDto.setTranslations(List.of(translationDto));

        Module module = new Module();
        module.setId(UUID.randomUUID());

        moduleTranslationService.saveTranslations(List.of(moduleDto), List.of(module));

        verify(translationRepository).saveAll(anyList());
    }

    @Test
    void saveTranslations_WithEmptyTranslations_ShouldNotSave() {
        ModuleCreateRequestDto moduleDto = new ModuleCreateRequestDto();
        moduleDto.setTranslations(Collections.emptyList());

        Module module = new Module();
        module.setId(UUID.randomUUID());

        moduleTranslationService.saveTranslations(List.of(moduleDto), List.of(module));

        verify(translationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void saveTranslations_WithNullTranslations_ShouldThrowException() {
        ModuleCreateRequestDto moduleDto = new ModuleCreateRequestDto();
        moduleDto.setTranslations(null);

        Module module = new Module();
        module.setId(UUID.randomUUID());

        assertThrows(NullPointerException.class, () -> {
            moduleTranslationService.saveTranslations(List.of(moduleDto), List.of(module));
        });

        verify(translationRepository, never()).saveAll(anyList());
    }

    @Test
    void findByModuleId_WithValidId_ShouldReturnTranslations() {
        UUID moduleId = UUID.randomUUID();
        ModuleTranslation translation = new ModuleTranslation();
        translation.setId(UUID.randomUUID());

        when(translationRepository.findByModuleId(moduleId)).thenReturn(List.of(translation));

        List<ModuleTranslation> result = moduleTranslationService.findByModuleId(moduleId);

        assertEquals(1, result.size());
        verify(translationRepository).findByModuleId(moduleId);
    }

    @Test
    void findByModuleId_WithNonExistingId_ShouldReturnEmptyList() {
        UUID nonExistingId = UUID.randomUUID();

        when(translationRepository.findByModuleId(nonExistingId)).thenReturn(Collections.emptyList());

        List<ModuleTranslation> result = moduleTranslationService.findByModuleId(nonExistingId);

        assertTrue(result.isEmpty());
        verify(translationRepository).findByModuleId(nonExistingId);
    }
}