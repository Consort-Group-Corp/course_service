package uz.consortgroup.course_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.course_service.entity.Course;

@Component
@Slf4j
public class CourseValidator {
    public void validateCourse(Course course) {
        if (course == null || course.getId() == null) {
            log.warn("Invalid course object: {}", course);
            throw new IllegalArgumentException("Course must be persisted before saving modules");
        }
        log.debug("Course validation passed: {}", course.getId());
    }
}
