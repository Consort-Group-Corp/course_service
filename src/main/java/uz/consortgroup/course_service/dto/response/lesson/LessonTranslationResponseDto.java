package uz.consortgroup.course_service.dto.response.lesson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.course_service.entity.enumeration.Language;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonTranslationResponseDto {
    private UUID id;
    private Language language;
    private String title;
    private String description;
}
