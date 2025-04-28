package uz.consortgroup.course_service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import java.nio.file.Path;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "video.storage")
public class VideoStorageProperties {
   private Path location;
   private DataSize maxFileSize;
}
