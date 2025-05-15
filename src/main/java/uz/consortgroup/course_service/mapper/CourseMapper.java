package uz.consortgroup.course_service.mapper;

import org.mapstruct.Mapper;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CourseResponseDto;
import uz.consortgroup.course_service.entity.Course;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CourseTranslationMapper.class, ModuleMapper.class})
public interface CourseMapper {
    CourseResponseDto toResponseDto(Course course);
    Course toEntity(CourseCreateRequestDto dto);
    List<CourseResponseDto> toResponseList(List<Course> courses);
}
