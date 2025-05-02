package uz.consortgroup.course_service.validator;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BulkValidationStrategy<T> {
    void validate(T dto, List<MultipartFile> files);
}