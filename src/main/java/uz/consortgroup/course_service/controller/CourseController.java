package uz.consortgroup.course_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uz.consortgroup.core.api.v1.dto.request.course.CourseCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.response.course.CourseResponseDto;
import uz.consortgroup.course_service.service.course.CourseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
@Validated
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponseDto create(@RequestBody @Valid CourseCreateRequestDto dto) {
        return courseService.create(dto);
    }
}
