package uz.consortgroup.course_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uz.consortgroup.core.api.v1.dto.course.response.course.CoursePurchaseValidationResponseDto;
import uz.consortgroup.course_service.service.course.CourseService;

import java.util.UUID;

@RestController
@RequestMapping("/internal/courses")
@RequiredArgsConstructor
public class CourseInternalController {
    private final CourseService courseService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{courseId}/purchase-validation")
    public CoursePurchaseValidationResponseDto validateCourserForPurchase(@PathVariable UUID courseId) {
        return courseService.validateCourseForPurchase(courseId);
    }
}
