package uz.consortgroup.course_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.core.api.v1.dto.course.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.course_service.exception.EmptyFileException;
import uz.consortgroup.course_service.exception.MismatchException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageBulkValidationStrategy implements BulkValidationStrategy<BulkImageUploadRequestDto> {

    @Override
    public void validate(BulkImageUploadRequestDto dto, List<MultipartFile> files) {
        if (dto.getImages() == null || dto.getImages().isEmpty()) {
            log.error("Image list in DTO is null or empty");
            throw new EmptyFileException("Image list cannot be null or empty");
        }
        if (files.size() != dto.getImages().size()) {
            log.error("Mismatch between number of files ({}) and number of items in DTO ({})", files.size(), dto.getImages().size());
            throw new MismatchException("Количество файлов и метаданных должно совпадать");
        }
    }
}