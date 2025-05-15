package uz.consortgroup.course_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.core.api.v1.dto.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.request.pdf.PdfFileUploadRequestDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PdfBulkValidationStrategyTest {

    @InjectMocks
    private PdfBulkValidationStrategy strategy;

    private BulkPdfFilesUploadRequestDto dto;
    private List<MultipartFile> files;

    @BeforeEach
    void setUp() {
        dto = new BulkPdfFilesUploadRequestDto();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new byte[1024]
        );
        files = List.of(file);
    }

    @Test
    void testValidateValidInput() {
        dto.setPdfs(List.of(new PdfFileUploadRequestDto()));
        assertDoesNotThrow(() -> strategy.validate(dto, files));
    }

    @Test
    void testValidateMultipleValidFiles() {
        MockMultipartFile file1 = new MockMultipartFile(
                "file1",
                "test1.pdf",
                "application/pdf",
                new byte[1024]
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file2",
                "test2.pdf",
                "application/pdf",
                new byte[1024]
        );
        files = List.of(file1, file2);
        dto.setPdfs(List.of(new PdfFileUploadRequestDto(), new PdfFileUploadRequestDto()));
        assertDoesNotThrow(() -> strategy.validate(dto, files));
    }


    @Test
    void testValidateEmptyPdfList() {
        dto.setPdfs(Collections.emptyList());
        assertThrows(IllegalArgumentException.class, () -> strategy.validate(dto, files));
    }

    @Test
    void testValidateNullPdfList() {
        dto.setPdfs(null);
        assertThrows(IllegalArgumentException.class, () -> strategy.validate(dto, files));
    }

    @Test
    void testValidateMismatchFileAndMetadataCount() {
        dto.setPdfs(List.of(
                new PdfFileUploadRequestDto(),
                new PdfFileUploadRequestDto()
        ));
        assertThrows(IllegalArgumentException.class, () -> strategy.validate(dto, files));
    }

    @Test
    void testValidateNoFilesWithNonEmptyDto() {
        dto.setPdfs(List.of(new PdfFileUploadRequestDto()));
        assertThrows(IllegalArgumentException.class, () -> strategy.validate(dto, Collections.emptyList()));
    }
}