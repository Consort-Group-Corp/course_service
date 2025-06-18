package uz.consortgroup.course_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CourseResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CourseTranslationResponseDto;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.entity.CourseTranslation;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CourseTranslationMapper.class, ModuleMapper.class})
public interface CourseMapper {
    CourseResponseDto toResponseDto(Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Course toEntity(CourseCreateRequestDto dto);
    List<CourseResponseDto> toResponseList(List<Course> courses);
}
