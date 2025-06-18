package uz.consortgroup.course_service.service.teacher;

import uz.consortgroup.core.api.v1.dto.course.response.course.TeacherShortDto;

import java.util.UUID;

public interface TeacherInfoService {
    TeacherShortDto getTeacherShortInfo(UUID userId);
}
