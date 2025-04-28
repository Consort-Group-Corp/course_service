package uz.consortgroup.course_service.dto.request.resource;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.entity.enumeration.ResourceType;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResourceCreateRequestDto {
    @NotNull
    private UUID lessonId;

    @NotNull
    private ResourceType resourceType;

    @NotNull
    private String fileUrl;

    private Long fileSize;

    private MimeType mimeType;

    @NotNull
    private Integer orderPosition;

    private List<ResourceTranslationRequestDto> translations;
}