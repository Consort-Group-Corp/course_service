package uz.consortgroup.course_service.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import uz.consortgroup.course_service.entity.enumeration.FileType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "storage")
@Data
public class StorageProperties {
    @NotNull(message = "Base directory must not be null")
    private Path baseDir;

    @NotEmpty(message = "File types must not be empty")
    private Map<String, FileTypeProperties> fileTypes;

    private FileTypeProperties defaultFileType;

    public StorageProperties() {
        this.defaultFileType = new FileTypeProperties();
        this.defaultFileType.setSubDir("others");
        this.defaultFileType.setMaxFileSize(DataSize.ofMegabytes(10));
    }

    public FileTypeProperties getFileType(FileType fileType) {
        String key = fileType.name().toLowerCase();
        return fileTypes.getOrDefault(key, defaultFileType);
    }

    @Data
    public static class FileTypeProperties {
        @NotNull(message = "Subdirectory must not be null")
        private String subDir;

        @NotNull(message = "Max file size must not be null")
        @Positive(message = "Max file size must be positive")
        private DataSize maxFileSize;

        private List<String> allowedMimeTypes;
        private List<String> allowedExtensions;

        public Path getLocation(Path baseDir) {
            return baseDir.resolve(subDir);
        }
    }
}