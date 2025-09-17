package uz.consortgroup.course_service.controller;

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
import uz.consortgroup.core.api.v1.dto.course.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.pdf.BulkPdfFilesUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.course_service.service.media.pdf.PdfFileService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/lessons")
@Validated
public class PdfFileController {
    private final PdfFileService fileService;

    @PostMapping(value = "/{lessonId}/pdfs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PdfFileUploadResponseDto uploadPdfFile(
            @PathVariable UUID lessonId,
            @RequestPart("metadata") @Valid PdfFileUploadRequestDto metadata,
            @RequestPart("file") MultipartFile file
    ) {
        return fileService.upload(lessonId, metadata, file);
    }

    @PostMapping(value = "/{lessonId}/pdfs/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BulkPdfFilesUploadResponseDto uploadPdfFiles(
            @PathVariable UUID lessonId,
            @RequestPart("metadata") @Valid BulkPdfFilesUploadRequestDto metadata,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return fileService.uploadBulk(lessonId, metadata, files);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{lessonId}/pdfs/{resourceId}")
    public void delete(@PathVariable UUID lessonId, @PathVariable UUID resourceId) {
        fileService.delete(lessonId, resourceId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{lessonId}/pdfs/bulk")
    public void deleteBulk(@PathVariable UUID lessonId, @RequestBody List<UUID> resourceIds) {
        fileService.deleteBulk(lessonId, resourceIds);
    }
}
