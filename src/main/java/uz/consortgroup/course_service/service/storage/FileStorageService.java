package uz.consortgroup.course_service.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileStorageService {
    String store(UUID courseId, UUID lessonId, MultipartFile file);
    List<String> storeMultiple(UUID courseId, UUID lessonId, List<MultipartFile> files);
}
