package uz.consortgroup.course_service.dto.request.image;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BulkImageUploadRequestDto {
    @NotEmpty(message = "Image list must not be empty")
    private List<ImageUploadRequestDto> images;
}
