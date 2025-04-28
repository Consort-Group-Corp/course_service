package uz.consortgroup.course_service.dto.request.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.course_service.dto.request.resource.ResourceCreateRequestDto;
import uz.consortgroup.course_service.entity.enumeration.LessonType;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LessonCreateRequestDto {
    private UUID lessonId;

    @NotNull(message = "Module ID is required")
    private UUID moduleId;

    @NotNull(message = "Order position is required")
    private Integer orderPosition;

    @NotNull(message = "Lesson type is required")
    private LessonType lessonType;

    @NotBlank(message = "Content URL is required")
    private String contentUrl;

    private Integer durationMinutes;

    @NotNull(message = "Is preview is required")
    private Boolean isPreview;

    private List<LessonTranslationRequestDto> translations;

    @NotNull(message = "Resources are required")
    @Size(min = 1, message = "At least one resource is required")
    private List<ResourceCreateRequestDto> resources;
}
