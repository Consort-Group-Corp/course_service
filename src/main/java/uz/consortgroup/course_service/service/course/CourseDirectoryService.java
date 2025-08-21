package uz.consortgroup.course_service.service.course;

import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCoverDto;

import java.util.List;
import java.util.UUID;

public interface CourseDirectoryService {
    List<CourseCoverDto> getCoverUrls(List<UUID> courseIds);
}
