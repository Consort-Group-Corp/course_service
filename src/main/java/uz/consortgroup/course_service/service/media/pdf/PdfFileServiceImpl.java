package uz.consortgroup.course_service.service.media.pdf;

import org.springframework.stereotype.Service;
import uz.consortgroup.course_service.dto.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.course_service.dto.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.course_service.dto.response.pdf.BulkPdfFilesUploadResponseDto;
import uz.consortgroup.course_service.dto.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.media.AbstractMediaUploadService;
import uz.consortgroup.course_service.service.media.processor.pdf.BulkPdfFilesUploadProcessor;
import uz.consortgroup.course_service.service.media.processor.pdf.PdfFilesUploadProcessor;
import uz.consortgroup.course_service.service.storage.FileStorageService;
import uz.consortgroup.course_service.validator.FileStorageValidator;

@Service
public class PdfFileServiceImpl extends AbstractMediaUploadService<PdfFileUploadRequestDto, PdfFileUploadResponseDto, BulkPdfFilesUploadRequestDto,
        BulkPdfFilesUploadResponseDto, PdfFilesUploadProcessor, BulkPdfFilesUploadProcessor> implements PdfFileService {
    public PdfFileServiceImpl(FileStorageService storage, LessonService lessonService, PdfFilesUploadProcessor pdfUploadProcessor,
                              BulkPdfFilesUploadProcessor bulkPdfUploadProcessor, FileStorageValidator fileStorageValidator) {
        super(storage, lessonService, pdfUploadProcessor, bulkPdfUploadProcessor, fileStorageValidator);
    }
}