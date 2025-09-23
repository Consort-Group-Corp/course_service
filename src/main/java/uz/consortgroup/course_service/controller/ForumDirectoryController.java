package uz.consortgroup.course_service.controller;


import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCoverDto;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseIdsRequest;
import uz.consortgroup.course_service.service.course.CourseDirectoryService;

import java.util.List;

@RestController
@RequestMapping("/internal/forum-directory")
@RequiredArgsConstructor
@Hidden
public class ForumDirectoryController {

    private final CourseDirectoryService courseDirectoryService;

    @PostMapping("/course-covers")
    public List<CourseCoverDto> getCourseCovers(@RequestBody CourseIdsRequest request) {
        return courseDirectoryService.getCoverUrls(request.getIds());
    }
}
