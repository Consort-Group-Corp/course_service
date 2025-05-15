package uz.consortgroup.course_service.service.media.processor;

import uz.consortgroup.core.api.v1.dto.enumeration.MimeType;

import java.util.List;
import java.util.UUID;

public interface BulkMediaUploadProcessor<SingleRequestDto, BulkRequestDto, BulkResponseDto> {
    BulkResponseDto processBulkUpload(UUID lessonId, BulkRequestDto dto, List<String> fileUrls, List<MimeType> mimeTypes, List<Long> fileSizes);
}