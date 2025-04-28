package uz.consortgroup.course_service.dto.response.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.course_service.dto.response.lesson.LessonResponseDto;
import uz.consortgroup.course_service.dto.response.module.ModuleResponseDto;
import uz.consortgroup.course_service.entity.enumeration.CourseStatus;
import uz.consortgroup.course_service.entity.enumeration.CourseType;
import uz.consortgroup.course_service.entity.enumeration.PriceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDto {
    private UUID id;
    private UUID authorId;
    private CourseType courseType;
    private PriceType priceType;
    private BigDecimal priceAmount;
    private BigDecimal discountPercent;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer accessDurationMin;
    private CourseStatus courseStatus;
    private String coverImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CourseTranslationResponseDto> translations;
    private List<ModuleResponseDto> modules;
}
