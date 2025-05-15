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
import uz.consortgroup.core.api.v1.dto.course.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.pdf.BulkPdfFilesUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.course_service.service.media.pdf.PdfFileService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "api/v1/pdf")
@Validated
public class PdfFileController {
    private final PdfFileService fileService;
    private final ObjectMapper objectMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/upload-pdf-file/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PdfFileUploadResponseDto uploadPdfFile(@PathVariable UUID lessonId,
                                                  @RequestPart("metadata") String metadataJson,
                                                  @RequestPart("file") MultipartFile file)
            throws JsonProcessingException {
        PdfFileUploadRequestDto metadata = objectMapper.readValue(metadataJson, PdfFileUploadRequestDto.class);
        return fileService.upload(lessonId, metadata, file);
    }

    @PostMapping(value = "/upload-pdf-files/{lessonId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BulkPdfFilesUploadResponseDto uploadPdfFiles(@PathVariable UUID lessonId,
                                                        @RequestPart("metadata") String metadataJson,
                                                        @RequestPart("files") List<MultipartFile> files) throws JsonProcessingException {
        BulkPdfFilesUploadRequestDto metadata = objectMapper.readValue(metadataJson, BulkPdfFilesUploadRequestDto.class);
        return fileService.uploadBulk(lessonId, metadata, files);
    }
}
