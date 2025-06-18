package uz.consortgroup.course_service.service.teacher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.consortgroup.core.api.v1.dto.course.response.course.TeacherShortDto;
import uz.consortgroup.core.api.v1.dto.user.response.UserShortInfoResponseDto;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.client.TeacherInfoClient;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TeacherInfoServiceImpl implements TeacherInfoService {
    private final TeacherInfoClient teacherInfoClient;

    @Override
    @AllAspect
    public TeacherShortDto getTeacherShortInfo(UUID userId) {
        UserShortInfoResponseDto raw = teacherInfoClient.getRawShortInfo(userId);
        String fullName = Stream.of(raw.getFirstName(), raw.getLastName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));

        return TeacherShortDto.builder()
                .id(raw.getId())
                .fullName(fullName)
                .position(raw.getPosition())
                .build();
    }
}
