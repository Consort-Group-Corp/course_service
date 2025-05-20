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
import uz.consortgroup.core.api.v1.dto.course.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.image.ImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.exception.MismatchException;
import uz.consortgroup.course_service.service.media.image.ImageService;

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

@WebMvcTest(ImageController.class)
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void uploadImage_Success() throws Exception {
        UUID lessonId = UUID.randomUUID();
        ImageUploadRequestDto metadata = ImageUploadRequestDto.builder()
            .orderPosition(1)
            .build();

        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(imageService.upload(eq(lessonId), any(), any()))
            .thenReturn(ImageUploadResponseDto.builder().build());

        mockMvc.perform(multipart("/api/v1/lessons/{lessonId}/images", lessonId)
                .file(file)
                .part(new MockPart("metadata",
                    objectMapper.writeValueAsString(metadata).getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isCreated());
    }

    @Test
    void uploadImagesBulk_Success() throws Exception {
        UUID lessonId = UUID.randomUUID();
        BulkImageUploadRequestDto metadata = BulkImageUploadRequestDto.builder()
            .images(List.of(ImageUploadRequestDto.builder().orderPosition(1).build()))
            .build();

        MockMultipartFile file1 = new MockMultipartFile(
            "files", "test1.jpg", "image/jpeg", "content1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
            "files", "test2.jpg", "image/jpeg", "content2".getBytes());

        when(imageService.uploadBulk(eq(lessonId), any(), any()))
            .thenReturn(BulkImageUploadResponseDto.builder().build());

        mockMvc.perform(multipart("/api/v1/lessons/{lessonId}/images/bulk", lessonId)
                .file(file1)
                .file(file2)
                .part(new MockPart("metadata", 
                    objectMapper.writeValueAsString(metadata).getBytes()))).andExpect(status().isCreated());
    }



    @Test
    void uploadImage_InvalidMetadata() throws Exception {
        UUID lessonId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "content".getBytes());

        String invalidJson = "{\"orderPosition\":1";

        MockPart metadataPart = new MockPart("metadata", invalidJson.getBytes());
        metadataPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(multipart("/api/v1/lessons/{lessonId}/images", lessonId)
                        .file(file)
                        .part(metadataPart))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadImage_MissingFile() throws Exception {
        UUID lessonId = UUID.randomUUID();
        ImageUploadRequestDto metadata = ImageUploadRequestDto.builder()
                .orderPosition(1)
                .build();

        MockPart metadataPart = new MockPart("metadata",
                objectMapper.writeValueAsString(metadata).getBytes());
        metadataPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(multipart("/api/v1/lessons/{lessonId}/images", lessonId)
                        .part(metadataPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required request part 'file' is not present"));
    }

    @Test
    void uploadImagesBulk_EmptyFilesList() throws Exception {
        UUID lessonId = UUID.randomUUID();
        BulkImageUploadRequestDto metadata = BulkImageUploadRequestDto.builder()
            .images(List.of(ImageUploadRequestDto.builder().build()))
            .build();

        mockMvc.perform(multipart("/api/v1/lessons/{lessonId}/images/bulk", lessonId)
                .part(new MockPart("metadata", 
                    objectMapper.writeValueAsString(metadata).getBytes())))
            .andExpect(status().isBadRequest());
    }

    @Test
    void uploadImagesBulk_MetadataMismatch() throws Exception {
        UUID lessonId = UUID.randomUUID();
        BulkImageUploadRequestDto metadata = BulkImageUploadRequestDto.builder()
                .images(List.of(
                        ImageUploadRequestDto.builder().orderPosition(1).build(),
                        ImageUploadRequestDto.builder().orderPosition(2).build()
                ))
                .build();

        doThrow(new MismatchException("Количество файлов не соответствует метаданным"))
                .when(imageService)
                .uploadBulk(eq(lessonId), any(BulkImageUploadRequestDto.class), anyList());

        MockPart metadataPart = new MockPart(
                "metadata",
                objectMapper.writeValueAsString(metadata).getBytes(StandardCharsets.UTF_8)
        );
        metadataPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        MockPart filePart = new MockPart(
                "files",
                "test.jpg",
                "content".getBytes(StandardCharsets.UTF_8)
        );
        filePart.getHeaders().setContentType(MediaType.IMAGE_JPEG);

        mockMvc.perform(multipart("/api/v1/lessons/{lessonId}/images/bulk", lessonId)
                        .part(metadataPart)
                        .part(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Количество файлов не соответствует метаданным"));
    }
}