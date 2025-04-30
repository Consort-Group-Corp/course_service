package uz.consortgroup.course_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uz.consortgroup.course_service.dto.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.course_service.dto.request.image.ImageUploadRequestDto;
import uz.consortgroup.course_service.dto.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.course_service.dto.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.service.media.image.ImageService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/image")
public class ImageController {
    private final ImageService imageService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/upload-image-file/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageUploadResponseDto uploadImage(@PathVariable UUID lessonId, @Valid @ModelAttribute ImageUploadRequestDto dto) {
        return imageService.upload(lessonId, dto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/upload-images-files/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BulkImageUploadResponseDto uploadImages(@PathVariable UUID lessonId, @Valid @ModelAttribute BulkImageUploadRequestDto dto) {
        return imageService.uploadImages(lessonId, dto);
    }
}
