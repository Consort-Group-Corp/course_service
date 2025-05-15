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
import uz.consortgroup.core.api.v1.dto.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.response.pdf.BulkPdfFilesUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.course_service.exception.MismatchException;
import uz.consortgroup.course_service.service.media.pdf.PdfFileService;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PdfFileController.class)
class PdfFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PdfFileService pdfFileService;

    @Test
    void uploadPdfFile_Success() throws Exception {
        UUID lessonId = UUID.randomUUID();
        PdfFileUploadRequestDto metadata = PdfFileUploadRequestDto.builder().build();
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "content".getBytes());

        when(pdfFileService.upload(eq(lessonId), any(), any()))
                .thenReturn(new PdfFileUploadResponseDto());

        mockMvc.perform(multipart("/api/v1/pdf/upload-pdf-file/{lessonId}", lessonId)
                        .file(file)
                        .part(new MockPart("metadata", objectMapper.writeValueAsString(metadata).getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }

    @Test
    void uploadPdfFile_InvalidMetadata() throws Exception {
        UUID lessonId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "content".getBytes());

        mockMvc.perform(multipart("/api/v1/pdf/upload-pdf-file/{lessonId}", lessonId)
                        .file(file)
                        .part(new MockPart("metadata", "invalid json".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadPdfFile_MissingFile() throws Exception {
        UUID lessonId = UUID.randomUUID();
        PdfFileUploadRequestDto metadata = PdfFileUploadRequestDto.builder().build();

        mockMvc.perform(multipart("/api/v1/pdf/upload-pdf-file/{lessonId}", lessonId)
                        .part(new MockPart("metadata", objectMapper.writeValueAsString(metadata).getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadPdfFiles_Success() throws Exception {
        UUID lessonId = UUID.randomUUID();
        BulkPdfFilesUploadRequestDto metadata = BulkPdfFilesUploadRequestDto.builder().build();
        MockMultipartFile file1 = new MockMultipartFile("files", "test1.pdf", "application/pdf", "content1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "test2.pdf", "application/pdf", "content2".getBytes());

        when(pdfFileService.uploadBulk(eq(lessonId), any(), any()))
                .thenReturn(new BulkPdfFilesUploadResponseDto());

        mockMvc.perform(multipart("/api/v1/pdf/upload-pdf-files/{lessonId}", lessonId)
                        .file(file1)
                        .file(file2)
                        .part(new MockPart("metadata", objectMapper.writeValueAsString(metadata).getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }

    @Test
    void uploadPdfFiles_MetadataMismatch() throws Exception {
        UUID lessonId = UUID.randomUUID();
        BulkPdfFilesUploadRequestDto metadata = BulkPdfFilesUploadRequestDto.builder().build();

        doThrow(new MismatchException("Количество файлов не соответствует метаданным"))
                .when(pdfFileService)
                .uploadBulk(eq(lessonId), any(BulkPdfFilesUploadRequestDto.class), anyList());

        MockPart metadataPart = new MockPart(
                "metadata",
                objectMapper.writeValueAsString(metadata).getBytes(StandardCharsets.UTF_8)
        );
        metadataPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        MockPart filePart = new MockPart(
                "files",
                "test.pdf",
                "content".getBytes(StandardCharsets.UTF_8)
        );
        filePart.getHeaders().setContentType(MediaType.APPLICATION_PDF);

        mockMvc.perform(multipart("/api/v1/pdf/upload-pdf-files/{lessonId}", lessonId)
                        .part(metadataPart)
                        .part(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Количество файлов не соответствует метаданным"));
    }


    @Test
    void uploadPdfFiles_EmptyFiles() throws Exception {
        UUID lessonId = UUID.randomUUID();
        BulkPdfFilesUploadRequestDto metadata = BulkPdfFilesUploadRequestDto.builder().build();

        mockMvc.perform(multipart("/api/v1/pdf/upload-pdf-files/{lessonId}", lessonId)
                        .part(new MockPart("metadata", objectMapper.writeValueAsString(metadata).getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
}