package uz.consortgroup.course_service.service.teacher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.consortgroup.core.api.v1.dto.course.response.course.TeacherShortDto;
import uz.consortgroup.core.api.v1.dto.user.response.UserShortInfoResponseDto;
import uz.consortgroup.course_service.client.TeacherInfoClient;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherInfoServiceImpl implements TeacherInfoService {
    private final TeacherInfoClient teacherInfoClient;

    @Override
    public TeacherShortDto getTeacherShortInfo(UUID userId) {
        log.info("Fetching teacher info for userId={}", userId);

        UserShortInfoResponseDto raw = teacherInfoClient.getRawShortInfo(userId);
        String fullName = Stream.of(raw.getFirstName(), raw.getLastName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));

        TeacherShortDto result = TeacherShortDto.builder()
                .id(raw.getId())
                .fullName(fullName)
                .position(raw.getPosition())
                .build();

        log.debug("Fetched teacher info: {}", result);
        return result;
    }
}
