package uz.consortgroup.course_service.dto.response.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.entity.enumeration.ResourceType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponseDto {
    private UUID id;
    private UUID lessonId;
    private ResourceType resourceType;
    private String fileUrl;
    private Long fileSize;
    private MimeType mimeType;
    private Integer orderPosition;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ResourceTranslationResponseDto> translations;
}
