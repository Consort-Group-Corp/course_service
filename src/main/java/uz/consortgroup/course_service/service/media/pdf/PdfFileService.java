package uz.consortgroup.course_service.service.media.pdf;

import uz.consortgroup.course_service.dto.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.course_service.dto.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.course_service.dto.response.pdf.BulkPdfFilesUploadResponseDto;
import uz.consortgroup.course_service.dto.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.course_service.service.media.processor.MediaUploadService;

public interface PdfFileService extends MediaUploadService<PdfFileUploadRequestDto, PdfFileUploadResponseDto, BulkPdfFilesUploadRequestDto, BulkPdfFilesUploadResponseDto> {
}
