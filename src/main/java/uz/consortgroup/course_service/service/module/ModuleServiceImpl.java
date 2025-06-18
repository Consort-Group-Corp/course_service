package uz.consortgroup.course_service.service.module;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
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
    @AllAspect
    public List<Module> saveModules(List<ModuleCreateRequestDto> modulesDto, Course course) {
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

        return moduleRepository.saveAll(modules);
    }

    @Override
    @Transactional(readOnly = true)
    @AllAspect
    public List<Module> findByCourseId(UUID courseId) {
        return moduleRepository.findByCourseId(courseId);
    }
}