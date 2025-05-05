package uz.consortgroup.course_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;
import uz.consortgroup.course_service.config.properties.StorageProperties;
import uz.consortgroup.course_service.dto.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.course_service.dto.request.image.ImageUploadRequestDto;
import uz.consortgroup.course_service.entity.enumeration.FileType;
import uz.consortgroup.course_service.exception.EmptyFileException;
import uz.consortgroup.course_service.exception.FileSizeLimitExceededException;
import uz.consortgroup.course_service.exception.UnsupportedFileExtensionException;
import uz.consortgroup.course_service.exception.UnsupportedFileTypeException;
import uz.consortgroup.course_service.exception.UnsupportedMimeTypeException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileStorageValidatorTest {

    @Mock
    private StorageProperties storageProperties;

    @InjectMocks
    private FileStorageValidator validator;
    private StorageProperties.FileTypeProperties imageProps = new StorageProperties.FileTypeProperties();


    @BeforeEach
    void setUp() {
        Map<String, StorageProperties.FileTypeProperties> fileTypeProperties = new HashMap<>();
        imageProps.setAllowedMimeTypes(List.of("image/jpeg", "image/png"));
        imageProps.setAllowedExtensions(List.of("jpg", "png"));
        imageProps.setMaxFileSize(DataSize.ofMegabytes(5));
        fileTypeProperties.put("IMAGE", imageProps);

        StorageProperties.FileTypeProperties videoProps = new StorageProperties.FileTypeProperties();
        videoProps.setAllowedMimeTypes(List.of("video/mp4"));
        videoProps.setAllowedExtensions(List.of("mp4"));
        videoProps.setMaxFileSize(DataSize.ofMegabytes(50));
        fileTypeProperties.put("VIDEO", videoProps);

        when(storageProperties.getFileTypes()).thenReturn(fileTypeProperties);


        validator = new FileStorageValidator(storageProperties);
        validator.init();
    }



    @Test
    void testValidateValidImageFile() {
        when(storageProperties.getFileType(FileType.IMAGE)).thenReturn(imageProps);

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[1024]
        );
        assertDoesNotThrow(() -> validator.validateFile(file, FileType.IMAGE));
    }

    @Test
    void testDetermineFileTypeForValidImage() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", new byte[1024]
        );
        FileType fileType = validator.determineFileType(file);
        assertEquals(FileType.IMAGE, fileType);
    }

    @Test
    void testValidateMultipleFilesNotEmpty() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[1024]
        );
        assertDoesNotThrow(() -> validator.validateMultipleFiles(List.of(file)));
    }

    @Test
    void testValidateBulkImageUpload() {
        ImageUploadRequestDto imgMeta = ImageUploadRequestDto.builder()
                .orderPosition(1)
                .build();

        BulkImageUploadRequestDto dto = BulkImageUploadRequestDto.builder()
                .images(List.of(imgMeta))
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "files", "test.jpg", "image/jpeg", new byte[1024]
        );

        assertDoesNotThrow(() -> validator.validateBulk(dto, List.of(file)));
    }

    @Test
    void testValidateEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[0]
        );
        assertThrows(EmptyFileException.class, () -> validator.validateFile(file, FileType.IMAGE));
    }

    @Test
    void testValidateFileExceedsSizeLimit() {
        when(storageProperties.getFileType(FileType.IMAGE)).thenReturn(imageProps);
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[6 * 1024 * 1024] // 6MB
        );
        assertThrows(FileSizeLimitExceededException.class, () -> validator.validateFile(file, FileType.IMAGE));
    }

    @Test
    void testValidateUnsupportedMimeType() {
        when(storageProperties.getFileType(FileType.IMAGE)).thenReturn(imageProps);
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/gif", new byte[1024]
        );
        assertThrows(UnsupportedMimeTypeException.class, () -> validator.validateFile(file, FileType.IMAGE));
    }

    @Test
    void testValidateUnsupportedFileExtension() {
        when(storageProperties.getFileType(FileType.IMAGE)).thenReturn(imageProps);
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.gif", "image/jpeg", new byte[1024]
        );
        assertThrows(UnsupportedFileExtensionException.class, () -> validator.validateFile(file, FileType.IMAGE));
    }

    @Test
    void testDetermineFileTypeUnsupported() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.xyz", "application/xyz", new byte[1024]
        );
        assertThrows(UnsupportedFileTypeException.class, () -> validator.determineFileType(file));
    }

    @Test
    void testValidateMultipleFilesEmpty() {
        assertThrows(EmptyFileException.class, () -> validator.validateMultipleFiles(Collections.emptyList()));
    }

    @Test
    void testValidateFileNoExtension() {
        when(storageProperties.getFileType(FileType.IMAGE)).thenReturn(imageProps);
        MockMultipartFile file = new MockMultipartFile(
                "file", "test", "image/jpeg", new byte[1024]
        );
        assertThrows(UnsupportedFileExtensionException.class, () -> validator.validateFile(file, FileType.IMAGE));
    }

    @Test
    void testValidateBulkUnsupportedDtoType() {
        Object invalidDto = new Object();
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[1024]
        );
        assertThrows(IllegalArgumentException.class, () -> validator.validateBulk(invalidDto, List.of(file)));
    }
}
