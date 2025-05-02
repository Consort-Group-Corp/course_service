package uz.consortgroup.course_service.service.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.course_service.config.properties.StorageProperties;
import uz.consortgroup.course_service.entity.enumeration.FileType;
import uz.consortgroup.course_service.exception.FileStorageException;
import uz.consortgroup.course_service.validator.FileStorageValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalFileStorageService implements FileStorageService {
    private final StorageProperties props;
    private final FileStorageValidator validator;

    @Override
    public String store(UUID courseId, UUID lessonId, MultipartFile file) {
        log.info("Storing file for courseId: {}, lessonId: {}", courseId, lessonId);
        FileType fileType = validator.determineFileType(file);
        determinedFileTypeLog(fileType);
        validator.validateFile(file, fileType);
        return storeFile(courseId, lessonId, file, fileType);
    }


    @Override
    public List<String> storeMultiple(UUID courseId, UUID lessonId, List<MultipartFile> files) {
        log.info("Storing multiple files for courseId: {}, lessonId: {}", courseId, lessonId);
        validator.validateMultipleFiles(files);

        List<String> filePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            FileType fileType = validator.determineFileType(file);
            determinedFileTypeLog(fileType);
            validator.validateFile(file, fileType);
            filePaths.add(storeFile(courseId, lessonId, file, fileType));
        }
        log.info("Stored {} files for courseId: {}, lessonId: {}", filePaths.size(), courseId, lessonId);
        return filePaths;
    }

    private String storeFile(UUID courseId, UUID lessonId, MultipartFile file, FileType type) {
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = getFileExtension(originalFilename); // Проверка на пустое расширение уже выполнена в validator

        String filename = UUID.randomUUID() + extension;
        log.debug("Generated filename: {}", filename);

        Path targetPath = prepareTargetPath(courseId, lessonId, filename, type);
        saveFileToDisk(file, targetPath);

        return formatMediaPath(courseId, lessonId, filename);
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex) : "";
    }

    private Path prepareTargetPath(UUID courseId, UUID lessonId, String filename, FileType type) {
        Path directory = props.getFileType(type).getLocation(props.getBaseDir())
                .resolve(courseId.toString())
                .resolve(lessonId.toString());
        log.debug("Prepared target directory: {}", directory);

        try {
            Files.createDirectories(directory);
            return directory.resolve(filename);
        } catch (IOException e) {
            log.error("Failed to create directory: {}", directory, e);
            throw new FileStorageException("Failed to create directory for file", e);
        }
    }

    private void saveFileToDisk(MultipartFile file, Path targetPath) {
        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("File saved to: {}", targetPath);
        } catch (IOException e) {
            log.error("Failed to save file: {}", targetPath.getFileName(), e);
            throw new FileStorageException("Failed to save file: " + targetPath.getFileName(), e);
        }
    }

    private String formatMediaPath(UUID courseId, UUID lessonId, String filename) {
        String path = "/media/" + courseId + "/" + lessonId + "/" + filename;
        log.debug("Formatted media path: {}", path);
        return path;
    }

    private void determinedFileTypeLog(FileType fileType) {
        log.debug("Determined file type: {}", fileType);
    }
}