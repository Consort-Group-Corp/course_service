package uz.consortgroup.course_service.service.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        if (file.isEmpty()) {
            throw new RuntimeException("Файл пуст");
        }

        if (file.getSize() > props.getMaxFileSize().toBytes()) {
            throw new RuntimeException("Размер файла превышает допустимый лимит: " + props.getMaxFileSize());
        }

        String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx > 0) {
            ext = original.substring(idx);
        }

        String filename = UUID.randomUUID() + ext;

        Path dir = props.getLocation()
                .resolve(courseId.toString())
                .resolve(lessonId.toString());
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/media/" + courseId + "/" + lessonId + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл " + original, e);
        }
    }

    @Override
    @AllAspect
    public List<String> storeMultiple(UUID courseId, UUID lessonId, List<MultipartFile> files) {
        if (files.isEmpty()) {
            throw new RuntimeException("Список файлов пуст");
        }

        List<String> filePaths = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new RuntimeException("Один из файлов пуст");
            }

            if (file.getSize() > props.getMaxFileSize().toBytes()) {
                throw new RuntimeException("Размер файла превышает допустимый лимит: " + props.getMaxFileSize());
            }

            String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String ext = "";
            int idx = original.lastIndexOf('.');
            if (idx > 0) {
                ext = original.substring(idx);
            }

            String filename = UUID.randomUUID() + ext;

            Path dir = props.getLocation()
                    .resolve(courseId.toString())
                    .resolve(lessonId.toString());
            try {
                Files.createDirectories(dir);
                Path target = dir.resolve(filename);
                Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

                filePaths.add("/media/" + courseId + "/" + lessonId + "/" + filename);
            } catch (IOException e) {
                throw new RuntimeException("Не удалось сохранить файл " + original, e);
            }
        }
        return filePaths;
    }

}
