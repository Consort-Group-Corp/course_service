package uz.consortgroup.course_service.mapper;

import org.mapstruct.Mapper;
import uz.consortgroup.course_service.dto.request.lesson.LessonTranslationRequestDto;
import uz.consortgroup.course_service.dto.response.lesson.LessonTranslationResponseDto;
import uz.consortgroup.course_service.entity.LessonTranslation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonTranslationMapper {
    LessonTranslationResponseDto toResponseDto(LessonTranslation entity);
    LessonTranslation toEntity(LessonTranslationRequestDto dto);
    List<LessonTranslationResponseDto> toResponseList(List<LessonTranslation> list);
}