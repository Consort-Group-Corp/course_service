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
import uz.consortgroup.core.api.v1.dto.course.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.image.ImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.service.media.image.ImageService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/lessons")
@Validated
@Tag(name = "Image", description = "Работа с изображениями")
public class ImageController {
    private final ImageService imageService;

    @PostMapping(value = "/{lessonId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ImageUploadResponseDto uploadImage(
            @PathVariable UUID lessonId,
            @RequestPart("metadata") @Valid ImageUploadRequestDto metadata,
            @Parameter(
                    name = "file",
                    description = "Поддерживаемые типы: image/jpeg, image/png. Макс. размер: 100MB. Разрешённые расширения: jpg, jpeg, png",
                    schema = @Schema(type = "string", format = "binary")
            )
            @RequestPart("file") MultipartFile file
    ) {
        return imageService.upload(lessonId, metadata, file);
    }

    @PostMapping(value = "/{lessonId}/images/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BulkImageUploadResponseDto uploadImages(
            @PathVariable UUID lessonId,
            @RequestPart("metadata") @Valid BulkImageUploadRequestDto metadata,
            @Parameter(
                    name = "files",
                    description = "Поддерживаемые типы: image/jpeg, image/png. Макс. размер каждого файла: 100MB. Разрешённые расширения: jpg, jpeg, png",
                    array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart("files") List<MultipartFile> files
    ) {
        return imageService.uploadBulk(lessonId, metadata, files);
    }

    @DeleteMapping("/{lessonId}/images/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID lessonId, @PathVariable UUID resourceId) {
        imageService.delete(lessonId, resourceId);
    }

    @DeleteMapping(value = "/{lessonId}/images/bulk", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBulk(@PathVariable UUID lessonId, @RequestBody List<UUID> imageIds) {
        imageService.deleteBulk(lessonId, imageIds);
    }
}
