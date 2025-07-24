package uz.consortgroup.course_service.service.module;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.repository.ModuleRepository;
import uz.consortgroup.course_service.validator.CourseValidator;
import uz.consortgroup.course_service.validator.ModuleValidator;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class ModuleServiceImpl implements ModuleService {
    private final ModuleRepository moduleRepository;
    private final CourseValidator courseValidator;
    private final ModuleValidator moduleValidator;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Module> saveModules(List<ModuleCreateRequestDto> modulesDto, Course course) {
        log.info("Saving modules for courseId={}", course.getId());
        courseValidator.validateCourse(course);
        moduleValidator.validateModules(modulesDto);

        List<Module> modules = modulesDto.stream()
                .map(dto -> Module.builder()
                        .course(course)
                        .moduleName(dto.getModuleName())
                        .orderPosition(dto.getOrderPosition())
                        .isActive(true)
                        .build())
                .toList();

        List<Module> saved = moduleRepository.saveAll(modules);
        log.debug("Saved {} modules for courseId={}", saved.size(), course.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Module> findByCourseId(UUID courseId) {
        log.info("Fetching modules for courseId={}", courseId);
        List<Module> modules = moduleRepository.findByCourseId(courseId);
        log.debug("Found {} modules for courseId={}", modules.size(), courseId);
        return modules;
    }
}
