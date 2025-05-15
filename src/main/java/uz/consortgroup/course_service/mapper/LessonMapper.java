package uz.consortgroup.course_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import uz.consortgroup.core.api.v1.dto.course.request.lesson.LessonCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.lesson.LessonResponseDto;
import uz.consortgroup.course_service.entity.Lesson;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LessonTranslationMapper.class, ResourceMapper.class})
public interface LessonMapper {
    @Mappings({
            @Mapping(target = "id",             source = "id"),
            @Mapping(target = "moduleId",      source = "module.id"),
            @Mapping(target = "orderPosition", source = "orderPosition"),
            @Mapping(target = "lessonType",    source = "lessonType"),
            @Mapping(target = "contentUrl",    source = "contentUrl"),
            @Mapping(target = "durationMinutes", source = "durationMinutes"),
            @Mapping(target = "isPreview",     source = "isPreview"),
            @Mapping(target = "createdAt",     source = "createdAt"),
            @Mapping(target = "updatedAt",     source = "updatedAt"),
            @Mapping(target = "translations",  source = "translations"),
    })
    LessonResponseDto toResponseDto(Lesson lesson);
    Lesson toEntity(LessonCreateRequestDto dto);
    List<LessonResponseDto> toResponseList(List<Lesson> lessons);
}