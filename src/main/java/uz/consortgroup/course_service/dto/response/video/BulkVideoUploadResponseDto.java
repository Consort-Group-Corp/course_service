package uz.consortgroup.course_service.dto.response.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BulkVideoUploadResponseDto {
    private List<VideoUploadResponseDto> videos;
}
