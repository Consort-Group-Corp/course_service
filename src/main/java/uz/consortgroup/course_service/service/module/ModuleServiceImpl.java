package uz.consortgroup.course_service.service.module;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.repository.ModuleRepository;
import jakarta.persistence.EntityManager;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ModuleServiceImpl implements ModuleService {
    private final ModuleRepository moduleRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @AllAspect
    public List<Module> saveModules(List<ModuleCreateRequestDto> modulesDto, Course course) {
        if (course.getId() == null) {
            throw new IllegalStateException("Course must be persisted before saving modules");
        }

        if (modulesDto == null || modulesDto.isEmpty()) {
            return List.of();
        }

        modulesDto.forEach(dto -> {
            if (dto.getModuleName() == null || dto.getOrderPosition() == null) {
                throw new IllegalArgumentException("Module name and order position must not be null");
            }
        });

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
}