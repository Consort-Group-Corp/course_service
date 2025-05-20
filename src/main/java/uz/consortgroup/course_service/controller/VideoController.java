package uz.consortgroup.course_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class VideoController {
    private final VideoService videoUploadService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "{lessonId}/videos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public VideoUploadResponseDto uploadVideo(
            @PathVariable UUID lessonId,
            @RequestPart("metadata") String metadataJson,
            @RequestPart("file") MultipartFile file) throws JsonProcessingException {

        VideoUploadRequestDto metadata = objectMapper.readValue(metadataJson, VideoUploadRequestDto.class);
        return videoUploadService.upload(lessonId, metadata, file);
    }

    @PostMapping(value = "{lessonId}/videos/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BulkVideoUploadResponseDto uploadVideos(
            @PathVariable UUID lessonId,
            @RequestPart("metadata") String metadataJson,
            @RequestPart("files") List<MultipartFile> files) throws JsonProcessingException {

        BulkVideoUploadRequestDto metadata = objectMapper.readValue(metadataJson, BulkVideoUploadRequestDto.class);
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
