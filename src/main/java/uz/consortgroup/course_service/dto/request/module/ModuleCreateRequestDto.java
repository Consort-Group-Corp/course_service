package uz.consortgroup.course_service.dto.request.module;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.course_service.dto.request.lesson.LessonCreateRequestDto;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ModuleCreateRequestDto {
    @NotNull(message = "Module name is required")
    private UUID courseId;

    @NotBlank
    private String moduleName;

    @NotNull(message = "Order position is required")
    private Integer orderPosition;

    @NotNull(message = "Is active is required")
    private Boolean isActive;
    private List<ModuleTranslationRequestDto> translations;
    private List<LessonCreateRequestDto> lessons;
}
