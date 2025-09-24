package uz.consortgroup.course_service.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.core.api.v1.dto.course.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.video.VideoUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.service.media.video.VideoService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lessons")
@Validated
@Tag(name = "Video", description = "работа с видео")
public class VideoController {
    private final VideoService videoUploadService;

    @PostMapping(value = "{lessonId}/videos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public VideoUploadResponseDto uploadVideo(
            @PathVariable UUID lessonId,
            @RequestPart("metadata") @Valid VideoUploadRequestDto metadata,
            @Parameter(
                    name = "file",
                    description = "Поддерживаемые типы: video/mp4, video/mpeg. Макс. размер: 1000MB. Разрешённые расширения: mp4, mpeg",
                    schema = @Schema(type = "string", format = "binary")
            )
            @RequestPart("file") MultipartFile file
    ) {
        return videoUploadService.upload(lessonId, metadata, file);
    }

    @PostMapping(value = "{lessonId}/videos/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BulkVideoUploadResponseDto uploadVideos(
            @PathVariable UUID lessonId,
            @RequestPart("metadata") @Valid BulkVideoUploadRequestDto metadata,
            @Parameter(
                    name = "files",
                    description = "Поддерживаемые типы: video/mp4, video/mpeg. Макс. размер каждого файла: 1000MB. Разрешённые расширения: mp4, mpeg",
                    array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart("files") List<MultipartFile> files
    ) {
        return videoUploadService.uploadBulk(lessonId, metadata, files);
    }

    @DeleteMapping("{lessonId}/videos/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID lessonId, @PathVariable UUID resourceId) {
        videoUploadService.delete(lessonId, resourceId);
    }

    @DeleteMapping("{lessonId}/videos/bulk")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBulk(@PathVariable UUID lessonId, @RequestBody List<UUID> resourceIds) {
        videoUploadService.deleteBulk(lessonId, resourceIds);
    }
}
