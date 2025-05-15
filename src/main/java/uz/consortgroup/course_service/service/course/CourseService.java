package uz.consortgroup.course_service.service.course;

import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CourseResponseDto;

public interface CourseService {
    CourseResponseDto create(CourseCreateRequestDto dto);
}
