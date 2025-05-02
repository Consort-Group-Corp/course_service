package uz.consortgroup.course_service.dto.request.image;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.course_service.dto.request.resource.ResourceTranslationRequestDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ImageUploadRequestDto {
    @NotNull(message = "Order position is required")
    private Integer orderPosition;
    private List<ResourceTranslationRequestDto> translations;
}
