package uz.consortgroup.course_service.validator;

import org.springframework.stereotype.Component;
import uz.consortgroup.course_service.entity.Course;

@Component
public class CourseValidator {
    public void validateCourse(Course course) {
        if (course == null || course.getId() == null) {
            throw new IllegalArgumentException("Course must be persisted before saving modules");
        }
    }
}
