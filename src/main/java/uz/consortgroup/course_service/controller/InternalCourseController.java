package uz.consortgroup.course_service.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uz.consortgroup.course_service.service.course.CourseService;

import java.util.UUID;

@RestController
@RequestMapping("/internal/courses")
@RequiredArgsConstructor
@Hidden
public class InternalCourseController {
    
    private final CourseService courseService;

    @GetMapping("/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean courseExistsById(@PathVariable UUID courseId) {
        return courseService.courseExistsById(courseId);
    }
}
