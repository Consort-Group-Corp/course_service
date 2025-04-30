package uz.consortgroup.course_service.dto.response.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkImageUploadResponseDto {
    private List<ImageUploadResponseDto> images;
}