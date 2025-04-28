package uz.consortgroup.course_service.service.course;


import uz.consortgroup.course_service.dto.request.course.CourseCreateRequestDto;
import uz.consortgroup.course_service.dto.response.course.CourseResponseDto;

public interface CourseService {
    CourseResponseDto create(CourseCreateRequestDto dto);
}
