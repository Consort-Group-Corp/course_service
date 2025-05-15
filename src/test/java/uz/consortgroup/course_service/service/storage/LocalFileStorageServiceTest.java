package uz.consortgroup.course_service.service.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.core.api.v1.dto.course.enumeration.FileType;
import uz.consortgroup.course_service.config.properties.StorageProperties;
import uz.consortgroup.course_service.exception.FileStorageException;
import uz.consortgroup.course_service.validator.FileStorageValidator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocalFileStorageServiceTest {

    @Mock
    private StorageProperties props;

    @Mock
    private FileStorageValidator validator;

    @InjectMocks
    private LocalFileStorageService localFileStorageService;

    private MultipartFile file;
    private StorageProperties.FileTypeProperties ftp;

    @BeforeEach
    void setUp() throws IOException {
        localFileStorageService = new LocalFileStorageService(props, validator);
        file = mock(MultipartFile.class);

        ftp = new StorageProperties.FileTypeProperties();
        ftp.setSubDir("images");
        ftp.setMaxFileSize(DataSize.ofMegabytes(5));
        ftp.setAllowedExtensions(List.of("jpg", "png", "jpeg"));
    }

    @Test
    void store_ShouldReturnFilePath() throws IOException {
        UUID courseId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();
        String filename = "test.jpg";
        FileType fileType = FileType.IMAGE;

        Path baseTestDir = Paths.get("media/uploads/test-" + UUID.randomUUID());
        when(props.getBaseDir()).thenReturn(baseTestDir);
        when(props.getFileType(FileType.IMAGE)).thenReturn(ftp);
        when(file.getOriginalFilename()).thenReturn(filename);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(validator.determineFileType(file)).thenReturn(fileType);

        String result = localFileStorageService.store(courseId, lessonId, file);

        assertNotNull(result);
        assertTrue(result.startsWith("/media/" + courseId + "/" + lessonId + "/"));
        verify(validator).validateFile(file, fileType);
        verify(props, atLeastOnce()).getFileType(FileType.IMAGE);

        String generatedFilename = Paths.get(result).getFileName().toString();
        Path storedFile = baseTestDir.resolve("images").resolve(courseId.toString()).resolve(lessonId.toString()).resolve(generatedFilename);
        assertTrue(Files.exists(storedFile));

        cleanDirectory(baseTestDir);
    }

    @Test
    void store_WithInvalidFile_ShouldThrowException() throws IOException {
        UUID courseId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();

        when(validator.determineFileType(file)).thenReturn(FileType.VIDEO);
        doThrow(new FileStorageException("Invalid file", new RuntimeException("underlying cause")))
                .when(validator).validateFile(file, FileType.VIDEO);

        assertThrows(FileStorageException.class, () -> {
            localFileStorageService.store(courseId, lessonId, file);
        });

        cleanDirectory(Paths.get("media/uploads"));
    }

    @Test
    void storeMultiple_ShouldReturnFilePaths() throws IOException {
        UUID courseId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();

        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);

        when(file1.getOriginalFilename()).thenReturn("a.jpg");
        when(file1.getInputStream()).thenReturn(new ByteArrayInputStream("1".getBytes()));

        when(file2.getOriginalFilename()).thenReturn("b.png");
        when(file2.getInputStream()).thenReturn(new ByteArrayInputStream("2".getBytes()));

        List<MultipartFile> files = List.of(file1, file2);

        Path baseTestDir = Paths.get("media/uploads/test-" + UUID.randomUUID());
        when(props.getBaseDir()).thenReturn(baseTestDir);
        when(props.getFileType(FileType.IMAGE)).thenReturn(ftp);
        when(validator.determineFileType(any())).thenReturn(FileType.IMAGE);

        List<String> paths = localFileStorageService.storeMultiple(courseId, lessonId, files);

        assertEquals(2, paths.size());
        verify(validator).validateMultipleFiles(files);
        verify(props, times(2)).getFileType(FileType.IMAGE);

        Path storedDir = baseTestDir.resolve("images").resolve(courseId.toString()).resolve(lessonId.toString());
        assertTrue(Files.exists(storedDir));

        cleanDirectory(baseTestDir);
    }

    @Test
    void storeMultiple_WithEmptyList_ShouldReturnEmptyList() throws IOException {
        UUID courseId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();

        List<String> result = localFileStorageService.storeMultiple(courseId, lessonId, List.of());

        assertTrue(result.isEmpty());
        verify(validator).validateMultipleFiles(List.of());

        cleanDirectory(Paths.get("media/uploads"));
    }

    @Test
    void storeMultiple_WhenDirectoryCreationFails_ShouldThrowException() throws Exception {
        UUID courseId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getInputStream()).thenThrow(new IOException("fail"));

        List<MultipartFile> files = List.of(file);

        Path baseTestDir = Paths.get("media/uploads/test-" + UUID.randomUUID());
        when(props.getBaseDir()).thenReturn(baseTestDir);
        when(props.getFileType(FileType.IMAGE)).thenReturn(ftp);
        when(validator.determineFileType(any())).thenReturn(FileType.IMAGE);

        assertThrows(FileStorageException.class, () -> {
            localFileStorageService.storeMultiple(courseId, lessonId, files);
        });

        verify(validator).validateMultipleFiles(files);

        cleanDirectory(baseTestDir);
    }

    @Test
    void storeMultiple_ShouldNotCreateTestDir() throws IOException {
        UUID courseId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();

        MultipartFile file1 = mock(MultipartFile.class);
        when(file1.getOriginalFilename()).thenReturn("a.jpg");
        when(file1.getInputStream()).thenReturn(new ByteArrayInputStream("1".getBytes()));

        List<MultipartFile> files = List.of(file1);

        Path baseTestDir = Paths.get("media/uploads/test-" + UUID.randomUUID());
        when(props.getBaseDir()).thenReturn(baseTestDir);
        when(props.getFileType(FileType.IMAGE)).thenReturn(ftp);
        when(validator.determineFileType(any())).thenReturn(FileType.IMAGE);

        Path testDir = Paths.get("test-dir");
        cleanDirectory(testDir);

        List<String> paths = localFileStorageService.storeMultiple(courseId, lessonId, files);

        assertEquals(1, paths.size());
        assertFalse(Files.exists(testDir));
        verify(validator).validateMultipleFiles(files);
        verify(props).getFileType(FileType.IMAGE);

        String generatedFilename = Paths.get(paths.get(0)).getFileName().toString();
        Path storedFile = baseTestDir.resolve("images").resolve(courseId.toString()).resolve(lessonId.toString()).resolve(generatedFilename);
        assertTrue(Files.exists(storedFile));

        cleanDirectory(baseTestDir);
    }

    private void cleanDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            try (Stream<Path> paths = Files.walk(dir)) {
                paths.sorted((p1, p2) -> -p1.compareTo(p2))
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
            Files.deleteIfExists(dir);
        }
    }
}