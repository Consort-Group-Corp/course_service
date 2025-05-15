package uz.consortgroup.course_service.service.media.pdf;

import uz.consortgroup.core.api.v1.dto.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.response.pdf.BulkPdfFilesUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.course_service.service.media.processor.MediaUploadService;

public interface PdfFileService extends MediaUploadService<PdfFileUploadRequestDto, PdfFileUploadResponseDto, BulkPdfFilesUploadRequestDto, BulkPdfFilesUploadResponseDto> {
}
