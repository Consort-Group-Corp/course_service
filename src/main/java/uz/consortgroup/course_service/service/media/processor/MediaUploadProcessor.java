package uz.consortgroup.course_service.service.media.processor;

import uz.consortgroup.course_service.entity.enumeration.MimeType;

import java.util.UUID;

public interface MediaUploadProcessor<RequestDto, ResponseDto> {
    ResponseDto processSingle(UUID lessonId, RequestDto dto, String fileUrl, MimeType mimeType, long fileSize);
}