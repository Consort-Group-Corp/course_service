package uz.consortgroup.course_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uz.consortgroup.course_service.dto.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.course_service.dto.request.video.VideoUploadRequestDto;
import uz.consortgroup.course_service.dto.response.video.BulkVideoUploadResponseDto;
import uz.consortgroup.course_service.dto.response.video.VideoUploadResponseDto;
import uz.consortgroup.course_service.exception.MismatchException;
import uz.consortgroup.course_service.service.media.video.VideoUploadService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoController.class)
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VideoUploadService videoUploadService;

    @Test
    void uploadVideo_Success() throws Exception {
        UUID lessonId = UUID.randomUUID();
        VideoUploadRequestDto metadata = VideoUploadRequestDto.builder().build();
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.mp4", "video/mp4", "video content".getBytes());

        when(videoUploadService.upload(eq(lessonId), any(), any()))
            .thenReturn(new VideoUploadResponseDto());

        mockMvc.perform(multipart("/api/v1/video/upload-video-file/{lessonId}", lessonId)
                .file(file)
                .part(new MockPart("metadata", 
                    objectMapper.writeValueAsString(metadata).getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated());
    }

    @Test
    void uploadVideo_InvalidMetadata() throws Exception {
        UUID lessonId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.mp4", "video/mp4", "content".getBytes());

        mockMvc.perform(multipart("/api/v1/video/upload-video-file/{lessonId}", lessonId)
                .file(file)
                .part(new MockPart("metadata", "invalid json".getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest());
    }

    @Test
    void uploadVideo_MissingFile() throws Exception {
        UUID lessonId = UUID.randomUUID();
        VideoUploadRequestDto metadata = VideoUploadRequestDto.builder().build();

        mockMvc.perform(multipart("/api/v1/video/upload-video-file/{lessonId}", lessonId)
                .part(new MockPart("metadata", 
                    objectMapper.writeValueAsString(metadata).getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest());
    }

    @Test
    void uploadVideos_Success() throws Exception {
        UUID lessonId = UUID.randomUUID();
        BulkVideoUploadRequestDto metadata = BulkVideoUploadRequestDto.builder().build();
        MockMultipartFile file1 = new MockMultipartFile(
            "files", "video1.mp4", "video/mp4", "content1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
            "files", "video2.mp4", "video/mp4", "content2".getBytes());

        when(videoUploadService.uploadBulk(eq(lessonId), any(), any()))
            .thenReturn(new BulkVideoUploadResponseDto());

        mockMvc.perform(multipart("/api/v1/video/upload-videos-files/{lessonId}", lessonId)
                .file(file1)
                .file(file2)
                .part(new MockPart("metadata", 
                    objectMapper.writeValueAsString(metadata).getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated());
    }

    @Test
    void uploadVideos_MetadataMismatch() throws Exception {
        UUID lessonId = UUID.randomUUID();
        BulkVideoUploadRequestDto metadata = BulkVideoUploadRequestDto.builder()
                .videos(List.of(
                        VideoUploadRequestDto.builder().build(),
                        VideoUploadRequestDto.builder().build()
                ))
                .build();

        doThrow(new MismatchException("Количество файлов не соответствует метаданным"))
                .when(videoUploadService)
                .uploadBulk(eq(lessonId), any(BulkVideoUploadRequestDto.class), anyList());

        MockPart metadataPart = new MockPart(
                "metadata",
                objectMapper.writeValueAsString(metadata).getBytes(StandardCharsets.UTF_8)
        );
        metadataPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        MockPart filePart = new MockPart(
                "files",
                "video.mp4",
                "content".getBytes(StandardCharsets.UTF_8)
        );
        filePart.getHeaders().setContentType(MediaType.valueOf("video/mp4"));

        mockMvc.perform(multipart("/api/v1/video/upload-videos-files/{lessonId}", lessonId)
                        .part(metadataPart)
                        .part(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Количество файлов не соответствует метаданным"));
    }


    @Test
    void uploadVideos_EmptyFiles() throws Exception {
        UUID lessonId = UUID.randomUUID();
        BulkVideoUploadRequestDto metadata = BulkVideoUploadRequestDto.builder().build();

        mockMvc.perform(multipart("/api/v1/video/upload-videos-files/{lessonId}", lessonId)
                .part(new MockPart("metadata", 
                    objectMapper.writeValueAsString(metadata).getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest());
    }
}