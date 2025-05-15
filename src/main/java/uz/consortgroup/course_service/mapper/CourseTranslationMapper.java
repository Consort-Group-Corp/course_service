package uz.consortgroup.course_service.mapper;

import org.mapstruct.Mapper;
import uz.consortgroup.core.api.v1.dto.request.course.CourseTranslationRequestDto;
import uz.consortgroup.core.api.v1.dto.response.course.CourseTranslationResponseDto;
import uz.consortgroup.course_service.entity.CourseTranslation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseTranslationMapper {
    CourseTranslationResponseDto toResponseDto(CourseTranslation entity);
    CourseTranslation toEntity(CourseTranslationRequestDto dto);
    List<CourseTranslationResponseDto> toResponseList(List<CourseTranslation> list);
}