package uz.consortgroup.course_service.service.module;

import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.entity.Module;

import java.util.List;

public interface ModuleService {
    List<Module> saveModules(List<ModuleCreateRequestDto> modulesDto, Course course);
}
