package uz.consortgroup.course_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.consortgroup.core.api.v1.dto.course.request.lesson.LessonTranslationRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.lesson.LessonTranslationResponseDto;
import uz.consortgroup.course_service.entity.LessonTranslation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonTranslationMapper {
    LessonTranslationResponseDto toResponseDto(LessonTranslation entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    LessonTranslation toEntity(LessonTranslationRequestDto dto);
    List<LessonTranslationResponseDto> toResponseList(List<LessonTranslation> list);
}