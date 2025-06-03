package uz.consortgroup.course_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseTranslationRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CourseTranslationResponseDto;
import uz.consortgroup.course_service.entity.CourseTranslation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseTranslationMapper {
    CourseTranslationResponseDto toResponseDto(CourseTranslation entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    CourseTranslation toEntity(CourseTranslationRequestDto dto);
    List<CourseTranslationResponseDto> toResponseList(List<CourseTranslation> list);
}