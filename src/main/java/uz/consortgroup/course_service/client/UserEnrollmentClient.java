package uz.consortgroup.course_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uz.consortgroup.core.api.v1.dto.course.response.course.EnrollmentFilterRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-service", contextId = "userEnrollmentClient", url = "${user.service.url}", configuration = FeignClientConfig.class)
public interface UserEnrollmentClient {


    @PostMapping("/enrollments/filter")
    List<UUID> filterEnrolled(@RequestBody EnrollmentFilterRequest request);
}