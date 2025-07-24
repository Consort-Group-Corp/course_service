package uz.consortgroup.course_service.service.module.translation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.entity.ModuleTranslation;
import uz.consortgroup.course_service.repository.ModuleTranslationRepository;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ModuleTranslationServiceImpl implements ModuleTranslationService {
    private final ModuleTranslationRepository translationRepository;

    @Override
    @Transactional
    public void saveTranslations(List<ModuleCreateRequestDto> modulesDto, List<Module> savedModules) {
        log.info("Saving module translations for {} modules", modulesDto.size());

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
        log.debug("Saved {} module translations", translations.size());
    }

    @Override
    public List<ModuleTranslation> findByModuleId(UUID moduleId) {
        log.info("Fetching translations for moduleId={}", moduleId);
        List<ModuleTranslation> result = translationRepository.findByModuleId(moduleId);
        log.debug("Found {} translations for moduleId={}", result.size(), moduleId);
        return result;
    }
}
