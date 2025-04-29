package uz.consortgroup.course_service.service.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.config.properties.VideoStorageProperties;

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
public class LocalFileStorageService implements FileStorageService {
    private final VideoStorageProperties props;

    @Override
    @AllAspect
    public String store(UUID courseId, UUID lessonId, MultipartFile file) {
        validateFile(file);
        return storeFile(courseId, lessonId, file);
    }

    @Override
    @AllAspect
    public List<String> storeMultiple(UUID courseId, UUID lessonId, List<MultipartFile> files) {
        if (files.isEmpty()) {
            throw new RuntimeException("Список файлов пуст");
        }

        List<String> filePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            validateFile(file);
            filePaths.add(storeFile(courseId, lessonId, file));
        }
        return filePaths;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Файл пуст");
        }

        if (file.getSize() > props.getMaxFileSize().toBytes()) {
            throw new RuntimeException("Размер файла превышает допустимый лимит: " + props.getMaxFileSize());
        }
    }

    private String storeFile(UUID courseId, UUID lessonId, MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID() + extension;

        Path targetPath = prepareTargetPath(courseId, lessonId, filename);
        saveFileToDisk(file, targetPath);

        return formatMediaPath(courseId, lessonId, filename);
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex) : "";
    }

    private Path prepareTargetPath(UUID courseId, UUID lessonId, String filename) {
        Path directory = props.getLocation()
                .resolve(courseId.toString())
                .resolve(lessonId.toString());

        try {
            Files.createDirectories(directory);
            return directory.resolve(filename);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для файла", e);
        }
    }

    private void saveFileToDisk(MultipartFile file, Path targetPath) {
        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл " + targetPath.getFileName(), e);
        }
    }

    private String formatMediaPath(UUID courseId, UUID lessonId, String filename) {
        return "/media/" + courseId + "/" + lessonId + "/" + filename;
    }
}