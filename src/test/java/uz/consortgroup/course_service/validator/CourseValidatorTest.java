package uz.consortgroup.course_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.consortgroup.course_service.entity.Course;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourseValidatorTest {

    private CourseValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CourseValidator();
    }

    @Test
    void testValidateCourse_ValidCourse_NoException() {
        Course course = new Course();
        course.setId(UUID.randomUUID());

        assertDoesNotThrow(() -> validator.validateCourse(course));
    }

    @Test
    void testValidateCourse_NullCourse_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> validator.validateCourse(null));
        assertEquals("Course must be persisted before saving modules", ex.getMessage());
    }

    @Test
    void testValidateCourse_CourseWithNullId_ThrowsException() {
        Course course = new Course();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> validator.validateCourse(course));
        assertEquals("Course must be persisted before saving modules", ex.getMessage());
    }
}
