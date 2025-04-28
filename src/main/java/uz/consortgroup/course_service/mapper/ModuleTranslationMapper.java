package uz.consortgroup.course_service.mapper;

import org.mapstruct.Mapper;
import uz.consortgroup.course_service.dto.request.module.ModuleTranslationRequestDto;
import uz.consortgroup.course_service.dto.response.module.ModuleTranslationResponseDto;
import uz.consortgroup.course_service.entity.ModuleTranslation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModuleTranslationMapper {
    ModuleTranslationResponseDto toResponseDto(ModuleTranslation entity);
    ModuleTranslation toEntity(ModuleTranslationRequestDto dto);
    List<ModuleTranslationResponseDto> toResponseList(List<ModuleTranslation> list);
}