package uz.consortgroup.course_service.dto.request.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.course_service.entity.enumeration.Language;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LessonTranslationRequestDto {
    @NotNull(message = "Language is required")
    private Language language;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
}
