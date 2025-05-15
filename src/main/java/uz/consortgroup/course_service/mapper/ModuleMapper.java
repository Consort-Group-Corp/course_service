package uz.consortgroup.course_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import uz.consortgroup.core.api.v1.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.response.module.ModuleResponseDto;
import uz.consortgroup.course_service.entity.Module;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ModuleTranslationMapper.class, LessonMapper.class})
public interface ModuleMapper {
    @Mappings({
            @Mapping(target = "id",           source = "id"),
            @Mapping(target = "courseId",     source = "course.id"),
            @Mapping(target = "moduleName",  source = "moduleName"),
            @Mapping(target = "orderPosition", source = "orderPosition"),
            @Mapping(target = "isActive",    source = "isActive"),
            @Mapping(target = "createdAt",   source = "createdAt"),
            @Mapping(target = "updatedAt",   source = "updatedAt"),
            @Mapping(target = "translations", source = "translations"),
            @Mapping(target = "lessons",     source = "lessons")
    })
    ModuleResponseDto toResponseDto(Module module);
    @Mapping(target = "course", ignore = true)
    Module toEntity(ModuleCreateRequestDto dto);
    List<ModuleResponseDto> toResponseList(List<Module> modules);
}