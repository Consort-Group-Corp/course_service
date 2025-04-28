package uz.consortgroup.course_service.dto.response.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.course_service.dto.response.resource.ResourceTranslationResponseDto;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoUploadResponseDto {
    private UUID resourceId;
    private String fileUrl;
    private Integer durationSeconds;
    private String resolution;
    private Integer orderPosition;
    private List<ResourceTranslationResponseDto> translations;
}
