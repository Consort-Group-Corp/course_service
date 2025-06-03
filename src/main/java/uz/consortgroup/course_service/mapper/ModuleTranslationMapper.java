package uz.consortgroup.course_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleTranslationRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.module.ModuleTranslationResponseDto;
import uz.consortgroup.course_service.entity.ModuleTranslation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModuleTranslationMapper {
    ModuleTranslationResponseDto toResponseDto(ModuleTranslation entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "module", ignore = true)
    ModuleTranslation toEntity(ModuleTranslationRequestDto dto);
    List<ModuleTranslationResponseDto> toResponseList(List<ModuleTranslation> list);
}