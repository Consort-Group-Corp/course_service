package uz.consortgroup.course_service.service.course;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.consortgroup.core.api.v1.dto.course.request.course.CourseCoverDto;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.repository.CourseRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseDirectoryServiceImpl implements CourseDirectoryService {

    private final CourseRepository courseRepository;

    @Override
    public List<CourseCoverDto> getCoverUrls(List<UUID> courseIds) {
        if (courseIds.isEmpty()) return List.of();

        if (courseIds.size() > 200) {
            log.error("Too many course IDs: {}", courseIds.size());
            throw new IllegalArgumentException("Too many course IDs");
        }

        log.info("Getting cover URLs for {} courses", courseIds.size());

        List<Course> courses = courseRepository.findAllById(courseIds);
        log.debug("Found {} courses", courses.size());

        Map<UUID, String> byId = courses.stream()
                .collect(Collectors.toMap(Course::getId, Course::getCoverImageUrl));

        List<CourseCoverDto> result = courseIds.stream()
                .map(id -> new CourseCoverDto(id, byId.get(id)))
                .toList();

        log.info("Returning cover URLs for {} courses", result.size());
        return result;
    }
}
