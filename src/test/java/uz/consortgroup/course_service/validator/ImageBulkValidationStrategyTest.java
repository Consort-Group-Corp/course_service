package uz.consortgroup.course_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.core.api.v1.dto.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.request.image.ImageUploadRequestDto;
import uz.consortgroup.course_service.exception.EmptyFileException;
import uz.consortgroup.course_service.exception.MismatchException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageBulkValidationStrategyTest {

    @InjectMocks
    private ImageBulkValidationStrategy strategy;

    private BulkImageUploadRequestDto dto;
    private List<MultipartFile> files;

    @BeforeEach
    void setUp() {
        dto = new BulkImageUploadRequestDto();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[1024]
        );
        files = List.of(file);
    }

    @Test
    void testValidateValidInput() {
        dto.setImages(List.of(new ImageUploadRequestDto()));
        assertDoesNotThrow(() -> strategy.validate(dto, files));
    }

    @Test
    void testValidateMultipleValidFiles() {
        MockMultipartFile file1 = new MockMultipartFile(
                "file1",
                "test1.jpg",
                "image/jpeg",
                new byte[1024]
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file2",
                "test2.jpg",
                "image/jpeg",
                new byte[1024]
        );
        files = List.of(file1, file2);
        dto.setImages(List.of(
                new ImageUploadRequestDto(),
                new ImageUploadRequestDto()
        ));
        assertDoesNotThrow(() -> strategy.validate(dto, files));
    }

    @Test
    void testValidateEmptyImageList() {
        dto.setImages(Collections.emptyList());
        assertThrows(EmptyFileException.class, () -> strategy.validate(dto, files));
    }

    @Test
    void testValidateNullImageList() {
        dto.setImages(null);
        assertThrows(EmptyFileException.class, () -> strategy.validate(dto, files));
    }

    @Test
    void testValidateMismatchFileAndMetadataCount() {
        dto.setImages(List.of(
                new ImageUploadRequestDto(),
                new ImageUploadRequestDto()
        ));
        assertThrows(MismatchException.class, () -> strategy.validate(dto, files));
    }

    @Test
    void testValidateNoFilesWithNonEmptyDto() {
        dto.setImages(List.of(new ImageUploadRequestDto()));
        assertThrows(MismatchException.class, () -> strategy.validate(dto, Collections.emptyList()));
    }
}