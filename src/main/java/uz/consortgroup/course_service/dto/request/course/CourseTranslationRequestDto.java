package uz.consortgroup.course_service.dto.request.course;

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
public class CourseTranslationRequestDto {
    private Language language;
    private String title;
    private String description;
    @NotNull(message = "Slug is required")
    private String slug;
}
