package uz.consortgroup.course_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.course_service.dto.request.pdf.BulkPdfFilesUploadRequestDto;

import java.util.List;

@Component
@Slf4j
public class PdfBulkValidationStrategy implements BulkValidationStrategy<BulkPdfFilesUploadRequestDto> {

    @Override
    public void validate(BulkPdfFilesUploadRequestDto dto, List<MultipartFile> files) {
        if (dto.getPdfs() == null || dto.getPdfs().isEmpty()) {
            log.error("Pdf list in DTO is null or empty");
            throw new IllegalArgumentException("Pdf list cannot be null or empty");
        }
        if (files.size() != dto.getPdfs().size()) {
            log.error("Mismatch between number of files ({}) and number of items in DTO ({})", files.size(), dto.getPdfs().size());
            throw new IllegalArgumentException("Number of files and pdfs in DTO must be the same");
        }
    }
}
