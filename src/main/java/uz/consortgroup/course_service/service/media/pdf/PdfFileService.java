package uz.consortgroup.course_service.service.media.pdf;

import uz.consortgroup.core.api.v1.dto.course.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.pdf.BulkPdfFilesUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.course_service.service.media.processor.MediaUploadService;

public interface PdfFileService extends MediaUploadService<PdfFileUploadRequestDto, PdfFileUploadResponseDto, BulkPdfFilesUploadRequestDto, BulkPdfFilesUploadResponseDto> {
}
