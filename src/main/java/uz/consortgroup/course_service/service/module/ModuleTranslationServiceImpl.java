package uz.consortgroup.course_service.service.module;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.course_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.course_service.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.entity.ModuleTranslation;
import uz.consortgroup.course_service.repository.ModuleTranslationRepository;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class ModuleTranslationServiceImpl implements ModuleTranslationService {
    private final ModuleTranslationRepository translationRepository;

    @Override
    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void saveTranslations(List<ModuleCreateRequestDto> modulesDto, List<Module> savedModules) {
        List<ModuleTranslation> translations = IntStream.range(0, modulesDto.size())
                .mapToObj(i -> {
                    ModuleCreateRequestDto dto = modulesDto.get(i);
                    Module module = savedModules.get(i);
                    return dto.getTranslations().stream()
                            .map(trDto -> ModuleTranslation.builder()
                                    .module(module)
                                    .language(trDto.getLanguage())
                                    .title(trDto.getTitle())
                                    .description(trDto.getDescription())
                                    .build()
                            );
                })
                .flatMap(Function.identity())
                .toList();

        translationRepository.saveAll(translations);
    }

    @Override
    @AllAspect
    public List<ModuleTranslation> findByModuleId(UUID moduleId) {
        return translationRepository.findByModuleId(moduleId);
    }
}
