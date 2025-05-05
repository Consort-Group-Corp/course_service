package uz.consortgroup.course_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.course_service.dto.request.video.BulkVideoUploadRequestDto;
import uz.consortgroup.course_service.dto.request.video.VideoUploadRequestDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VideoBulkValidationStrategyTest {

    @InjectMocks
    private VideoBulkValidationStrategy strategy;

    private BulkVideoUploadRequestDto dto;
    private List<MultipartFile> files;

    @BeforeEach
    void setUp() {
        dto = new BulkVideoUploadRequestDto();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.mp4",
                "video/mp4",
                new byte[1024]
        );
        files = List.of(file);
    }

    // Positive Tests
    @Test
    void testValidateValidInput() {
        dto.setVideos(List.of(new VideoUploadRequestDto()));
        assertDoesNotThrow(() -> strategy.validate(dto, files));
    }

    @Test
    void testValidateMultipleValidFiles() {
        MockMultipartFile file1 = new MockMultipartFile(
                "file1",
                "test1.mp4",
                "video/mp4",
                new byte[1024]
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file2",
                "test2.mp4",
                "video/mp4",
                new byte[1024]
        );
        files = List.of(file1, file2);
        dto.setVideos(List.of(new VideoUploadRequestDto(), new VideoUploadRequestDto()));
        assertDoesNotThrow(() -> strategy.validate(dto, files));
    }

    // Negative Tests
    @Test
    void testValidateEmptyVideoList() {
        dto.setVideos(Collections.emptyList());
        assertThrows(IllegalArgumentException.class, () -> strategy.validate(dto, files));
    }

    @Test
    void testValidateNullVideoList() {
        dto.setVideos(null);
        assertThrows(IllegalArgumentException.class, () -> strategy.validate(dto, files));
    }

    @Test
    void testValidateMismatchFileAndMetadataCount() {
        dto.setVideos(List.of(
                new VideoUploadRequestDto(),
                new VideoUploadRequestDto()
        ));
        assertThrows(IllegalArgumentException.class, () -> strategy.validate(dto, files));
    }

    @Test
    void testValidateNoFilesWithNonEmptyDto() {
        dto.setVideos(List.of(new VideoUploadRequestDto()));
        assertThrows(IllegalArgumentException.class, () -> strategy.validate(dto, Collections.emptyList()));
    }
}