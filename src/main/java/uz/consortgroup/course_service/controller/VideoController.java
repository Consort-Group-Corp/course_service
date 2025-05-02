    package uz.consortgroup.course_service.controller;

    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestPart;
    import org.springframework.web.bind.annotation.ResponseStatus;
    import org.springframework.web.bind.annotation.RestController;
    import org.springframework.web.multipart.MultipartFile;
    import uz.consortgroup.course_service.dto.request.video.BulkVideoUploadRequestDto;
    import uz.consortgroup.course_service.dto.request.video.VideoUploadRequestDto;
    import uz.consortgroup.course_service.dto.response.video.BulkVideoUploadResponseDto;
    import uz.consortgroup.course_service.dto.response.video.VideoUploadResponseDto;
    import uz.consortgroup.course_service.service.media.video.VideoUploadService;

    import java.util.List;
    import java.util.UUID;

    @RestController
    @RequiredArgsConstructor
    @RequestMapping("api/v1/video")
    @Validated
    public class VideoController {
        private final VideoUploadService videoUploadService;
        private final ObjectMapper objectMapper;

        @PostMapping(value = "/upload-video-file/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @ResponseStatus(HttpStatus.CREATED)
        public VideoUploadResponseDto uploadVideo(
                @PathVariable UUID lessonId,
                @RequestPart("metadata") String metadataJson,
                @RequestPart("file") MultipartFile file) throws JsonProcessingException {

            VideoUploadRequestDto metadata = objectMapper.readValue(metadataJson, VideoUploadRequestDto.class);
            return videoUploadService.upload(lessonId, metadata, file);
        }

        @PostMapping(value="/upload-videos-files/{lessonId}", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
        @ResponseStatus(HttpStatus.CREATED)
        public BulkVideoUploadResponseDto uploadVideos(
                @PathVariable UUID lessonId,
                @RequestPart("metadata") String metadataJson,
                @RequestPart("files") List<MultipartFile> files) throws JsonProcessingException {

            BulkVideoUploadRequestDto metadata = objectMapper.readValue(metadataJson, BulkVideoUploadRequestDto.class);
            return videoUploadService.uploadBulk(lessonId, metadata, files);
        }

    }
