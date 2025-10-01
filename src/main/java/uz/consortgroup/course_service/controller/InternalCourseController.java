package uz.consortgroup.course_service.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uz.consortgroup.core.api.v1.dto.course.response.course.CoursePurchaseValidationResponseDto;
import uz.consortgroup.course_service.service.course.CourseService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/courses")
@RequiredArgsConstructor
@Hidden
public class InternalCourseController {
    
    private final CourseService courseService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{courseId}/purchase-validation")
    public CoursePurchaseValidationResponseDto validateCourserForPurchase(@PathVariable UUID courseId) {
        return courseService.validateCourseForPurchase(courseId);
    }


    @GetMapping("/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean courseExistsById(@PathVariable UUID courseId) {
        return courseService.courseExistsById(courseId);
    }

    @GetMapping("/{courseId}/mentor")
    @ResponseStatus(HttpStatus.OK)
    public UUID getMentorId(@PathVariable UUID courseId) {
        return courseService.getMentorIdByCourseId(courseId);
    }


    @PostMapping("/{courseId}/enrolled/check")
    @ResponseStatus(HttpStatus.OK)
    public List<UUID> checkEnrolled(@PathVariable UUID courseId,
                                    @RequestBody List<UUID> userIds) {
        return courseService.filterEnrolledUserIds(courseId, userIds);
    }

}
