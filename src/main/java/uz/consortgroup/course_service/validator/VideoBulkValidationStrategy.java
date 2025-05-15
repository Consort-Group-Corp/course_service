package uz.consortgroup.course_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.core.api.v1.dto.course.request.video.BulkVideoUploadRequestDto;

import java.util.List;

@Slf4j
@Component
public class VideoBulkValidationStrategy implements BulkValidationStrategy<BulkVideoUploadRequestDto> {

    @Override
    public void validate(BulkVideoUploadRequestDto dto, List<MultipartFile> files) {
        if (dto.getVideos() == null || dto.getVideos().isEmpty()) {
            log.error("Video list in DTO is null or empty");
            throw new IllegalArgumentException("Video list cannot be null or empty");
        }
        if (files.size() != dto.getVideos().size()) {
            log.error("Mismatch between number of files ({}) and number of items in DTO ({})", files.size(), dto.getVideos().size());
            throw new IllegalArgumentException("Количество файлов и видео в DTO должны совпадать");
        }
    }
}