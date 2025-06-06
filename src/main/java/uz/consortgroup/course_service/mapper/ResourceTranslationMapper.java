package uz.consortgroup.course_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;
import uz.consortgroup.core.api.v1.dto.course.request.resource.ResourceTranslationRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.resource.ResourceTranslationResponseDto;
import uz.consortgroup.course_service.entity.ResourceTranslation;

import java.util.List;

@Mapper(componentModel = "spring")
@Primary
public interface ResourceTranslationMapper {
    ResourceTranslationResponseDto toResponseDto(ResourceTranslation entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "resource", ignore = true)
    ResourceTranslation toEntity(ResourceTranslationRequestDto dto);
    List<ResourceTranslationResponseDto> toResponseList(List<ResourceTranslation> list);
}