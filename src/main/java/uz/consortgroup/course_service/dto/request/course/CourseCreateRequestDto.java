package uz.consortgroup.course_service.dto.request.course;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.course_service.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.enumeration.CourseStatus;
import uz.consortgroup.course_service.entity.enumeration.CourseType;
import uz.consortgroup.course_service.entity.enumeration.PriceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CourseCreateRequestDto {
    @NotNull
    private UUID authorId;

    @NotNull(message = "Course type is required")
    private CourseType courseType;

    @NotNull(message = "Price type is required")
    private PriceType priceType;

    private BigDecimal priceAmount;

    private BigDecimal discountPercent;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer accessDurationMin;

    @NotNull(message = "Course status is required")
    private CourseStatus courseStatus;

    private String coverImageUrl;

    @NotEmpty(message = "Course translations are required")
    private List<CourseTranslationRequestDto> translations;

    private List<ModuleCreateRequestDto> modules;
}
