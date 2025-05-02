package uz.consortgroup.course_service.validator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.course_service.config.properties.StorageProperties;
import uz.consortgroup.course_service.dto.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.course_service.dto.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.course_service.entity.enumeration.FileType;
import uz.consortgroup.course_service.exception.EmptyFileException;
import uz.consortgroup.course_service.exception.FileSizeLimitExceededException;
import uz.consortgroup.course_service.exception.UnsupportedFileExtensionException;
import uz.consortgroup.course_service.exception.UnsupportedFileTypeException;
import uz.consortgroup.course_service.exception.UnsupportedMimeTypeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileStorageValidator {
    private final StorageProperties storageProperties;
    private Map<String, FileType> mimeTypeToFileType;
    private Map<String, FileType> extensionToFileType;
    private final Map<Class<?>, BulkValidationStrategy<?>> bulkValidationStrategies = new HashMap<>();

    @PostConstruct
    public void init() {
        mimeTypeToFileType = new HashMap<>();
        extensionToFileType = new HashMap<>();

        Map<String, StorageProperties.FileTypeProperties> fileTypes = storageProperties.getFileTypes();
        for (Map.Entry<String, StorageProperties.FileTypeProperties> entry : fileTypes.entrySet()) {
            FileType fileType = FileType.valueOf(entry.getKey().toUpperCase());
            StorageProperties.FileTypeProperties props = entry.getValue();

            List<String> allowedMimeTypes = props.getAllowedMimeTypes();
            if (allowedMimeTypes != null) {
                for (String mimeType : allowedMimeTypes) {
                    mimeTypeToFileType.put(mimeType, fileType);
                }
            }

            List<String> allowedExtensions = props.getAllowedExtensions();
            if (allowedExtensions != null) {
                for (String extension : allowedExtensions) {
                    extensionToFileType.put(extension.toLowerCase(), fileType);
                }
            }
        }

        bulkValidationStrategies.put(BulkImageUploadRequestDto.class, new ImageBulkValidationStrategy());
        bulkValidationStrategies.put(BulkVideoUploadRequestDto.class, new VideoBulkValidationStrategy());
    }

    @SuppressWarnings("unchecked")
    public <T> void validateBulk(T dto, List<MultipartFile> files) {
        BulkValidationStrategy<T> strategy = (BulkValidationStrategy<T>) bulkValidationStrategies.get(dto.getClass());
        if (strategy == null) {
            log.error("No validation strategy found for DTO type: {}", dto.getClass().getName());
            throw new IllegalArgumentException("No validation strategy for DTO type: " + dto.getClass().getName());
        }
        strategy.validate(dto, files);
    }

    public void validateMultipleFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            log.error("File list is null or empty");
            throw new EmptyFileException("Empty file list");
        }
    }

    public FileType determineFileType(MultipartFile file) {
        String mimeType = file.getContentType();
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        String extension = getFileExtension(originalFilename);

        if (extension.isEmpty()) {
            fileExtensionLog(originalFilename);
            throw new UnsupportedFileExtensionException("File has no extension: " + originalFilename);
        }

        String ext = extension.substring(1).toLowerCase();
        log.debug("Determining file type for file: {}, MIME type: {}, extension: {}", originalFilename, mimeType, ext);

        if (mimeType != null && mimeTypeToFileType.containsKey(mimeType)) {
            log.debug("File type determined by MIME type: {}", mimeTypeToFileType.get(mimeType));
            return mimeTypeToFileType.get(mimeType);
        }

        if (extensionToFileType.containsKey(ext)) {
            log.debug("File type determined by extension: {}", extensionToFileType.get(ext));
            return extensionToFileType.get(ext);
        }

        log.error("Unsupported file type for file: {}, MIME type: {}, extension: {}", originalFilename, mimeType, ext);
        throw new UnsupportedFileTypeException(String.format("Unsupported file type for file: %s (MIME type: %s, extension: %s)", originalFilename, mimeType, ext));
    }

    public void validateFile(MultipartFile file, FileType fileType) {
        if (file == null || file.isEmpty()) {
            log.error("File is null or empty");
            throw new EmptyFileException("Empty file");
        }

        StorageProperties.FileTypeProperties fileTypeProps = storageProperties.getFileType(fileType);

        if (file.getSize() > fileTypeProps.getMaxFileSize().toBytes()) {
            log.error("File size exceeds the limit: {} (actual size: {} bytes)", fileTypeProps.getMaxFileSize(), file.getSize());
            throw new FileSizeLimitExceededException(String.format("File size exceeds the limit: %s", fileTypeProps.getMaxFileSize()));
        }

        String mimeType = file.getContentType();
        List<String> allowedMimeTypes = fileTypeProps.getAllowedMimeTypes();
        if (allowedMimeTypes != null && !allowedMimeTypes.isEmpty() && (mimeType == null || !allowedMimeTypes.contains(mimeType))) {
            log.error("Unsupported MIME type: {}. Allowed MIME types: {}", mimeType, allowedMimeTypes);
            throw new UnsupportedMimeTypeException(String.format("Unsupported MIME type: %s. Allowed MIME types: %s", mimeType, allowedMimeTypes));
        }

        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        String extension = getFileExtension(originalFilename);
        if (extension.isEmpty()) {
            fileExtensionLog(originalFilename);
            throw new UnsupportedFileExtensionException("File has no extension: " + originalFilename);
        }

        List<String> allowedExtensions = fileTypeProps.getAllowedExtensions();
        String ext = extension.substring(1).toLowerCase();
        if (allowedExtensions != null && !allowedExtensions.isEmpty() && !allowedExtensions.contains(ext)) {
            log.error("Unsupported file extension: {}. Allowed extensions: {}", extension, allowedExtensions);
            throw new UnsupportedFileExtensionException(String.format("Unsupported file extension: %s. Allowed extensions: %s", extension, allowedExtensions));
        }
    }

    private static void fileExtensionLog(String originalFilename) {
        log.error("File has no extension: {}", originalFilename);
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex) : "";
    }
}