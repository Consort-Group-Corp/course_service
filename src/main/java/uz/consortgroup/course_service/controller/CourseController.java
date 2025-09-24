package uz.consortgroup.course_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uz.consortgroup.core.api.v1.dto.course.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CoursePreviewResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.course.CourseResponseDto;
import uz.consortgroup.course_service.service.course.CourseService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
@Validated
@Tag(name = "Course", description = "Работа с курсами")
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponseDto create(@RequestBody @Valid CourseCreateRequestDto dto) {
        return courseService.create(dto);
    }

    @GetMapping("/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public CourseResponseDto getCourseTitleById(@PathVariable UUID courseId) {
       return courseService.getCourseById(courseId);
    }

    @GetMapping("/{courseId}/preview")
    @ResponseStatus(HttpStatus.OK)
    public CoursePreviewResponseDto getCoursePreview(@PathVariable UUID courseId,
                                                     @RequestParam(defaultValue = "RU") Language language) {
        return courseService.getCoursePreview(courseId, language);
    }

    @DeleteMapping("/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID courseId) {
        courseService.delete(courseId);
    }
}
