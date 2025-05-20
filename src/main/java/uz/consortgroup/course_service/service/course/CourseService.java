package uz.consortgroup.course_service.service.course;

import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CourseResponseDto;

import java.util.UUID;

public interface CourseService {
    CourseResponseDto create(CourseCreateRequestDto dto);
    void delete(UUID courseId);
}
