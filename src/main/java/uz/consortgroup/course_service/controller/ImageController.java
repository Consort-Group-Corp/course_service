package uz.consortgroup.course_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.course_service.dto.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.course_service.dto.request.image.ImageUploadRequestDto;
import uz.consortgroup.course_service.dto.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.course_service.dto.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.service.media.image.ImageService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/image")
public class ImageController {
    private final ImageService imageService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/upload-image-file/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ImageUploadResponseDto uploadImage(
            @PathVariable UUID lessonId,
            @RequestPart("metadata") String metadataJson,
            @RequestPart("file") MultipartFile file
    ) throws JsonProcessingException {
        ImageUploadRequestDto metadata = objectMapper.readValue(metadataJson, ImageUploadRequestDto.class);
        return imageService.upload(lessonId, metadata, file);
    }

    @PostMapping(value = "/upload-images-files/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BulkImageUploadResponseDto uploadImages(
            @PathVariable UUID lessonId,
            @RequestPart("metadata") String metadataJson,
            @RequestPart("files") List<MultipartFile> files
    ) throws JsonProcessingException {
        BulkImageUploadRequestDto metadata = objectMapper.readValue(metadataJson, BulkImageUploadRequestDto.class);
        return imageService.uploadBulk(lessonId, metadata, files);
    }
}
