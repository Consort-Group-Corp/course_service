package uz.consortgroup.course_service.service.media;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

public interface MediaService<SingleRequestDto, SingleResponseDto, BulkRequestDto, BulkResponseDto> {
    SingleResponseDto upload(UUID lessonId, SingleRequestDto dto, MultipartFile file);
    BulkResponseDto uploadBulk(UUID lessonId, BulkRequestDto dto, List<MultipartFile> files);
    void delete(UUID lessonId, UUID resourceId);
    void deleteBulk(UUID lessonId, List<UUID> resourceIds);
}