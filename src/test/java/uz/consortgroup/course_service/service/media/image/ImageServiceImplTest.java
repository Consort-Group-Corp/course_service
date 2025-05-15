package uz.consortgroup.course_service.service.media.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.core.api.v1.dto.course.enumeration.FileType;
import uz.consortgroup.core.api.v1.dto.course.request.image.BulkImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.image.ImageUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.image.BulkImageUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.image.ImageUploadResponseDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.media.processor.image.BulkImageUploadProcessor;
import uz.consortgroup.course_service.service.media.processor.image.ImageUploadProcessor;
import uz.consortgroup.course_service.service.storage.FileStorageService;
import uz.consortgroup.course_service.validator.FileStorageValidator;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {

    @Mock
    private FileStorageService storage;

    @Mock
    private LessonService lessonService;

    @Mock
    private ImageUploadProcessor imageUploadProcessor;

    @Mock
    private BulkImageUploadProcessor bulkImageUploadProcessor;

    @Mock
    private FileStorageValidator fileStorageValidator;

    @Mock
    private MultipartFile imageFile;

    @Mock
    private List<MultipartFile> imageFiles;

    @InjectMocks
    private ImageServiceImpl imageService;

    @BeforeEach
    void setUp() {
        imageService = new ImageServiceImpl(
            storage,
            lessonService,
            imageUploadProcessor,
            bulkImageUploadProcessor,
            fileStorageValidator
        );
    }

    @Test
    void upload_WithValidImage_ShouldReturnResponse() {
        UUID lessonId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        ImageUploadRequestDto requestDto = new ImageUploadRequestDto();
        ImageUploadResponseDto expectedResponse = new ImageUploadResponseDto();
        String imageUrl = "http://example.com/image.jpg";
        Lesson lesson = createTestLesson(courseId);

        when(imageFile.getSize()).thenReturn(1024L);
        when(imageFile.getContentType()).thenReturn("image/jpeg");
        when(fileStorageValidator.determineFileType(imageFile)).thenReturn(FileType.IMAGE);
        when(lessonService.getLessonEntity(lessonId)).thenReturn(lesson);
        when(storage.store(courseId, lessonId, imageFile)).thenReturn(imageUrl);
        when(imageUploadProcessor.processSingle(
            eq(lessonId), any(), eq(imageUrl), any(), eq(1024L))
        ).thenReturn(expectedResponse);

        ImageUploadResponseDto result = imageService.upload(lessonId, requestDto, imageFile);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(fileStorageValidator).validateFile(imageFile, FileType.IMAGE);
        verify(storage).store(courseId, lessonId, imageFile);
    }

    @Test
    void uploadBulk_WithValidImages_ShouldReturnResponse() {
        UUID lessonId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        BulkImageUploadRequestDto requestDto = new BulkImageUploadRequestDto();
        BulkImageUploadResponseDto expectedResponse = new BulkImageUploadResponseDto();
        List<String> imageUrls = List.of("url1.jpg", "url2.jpg");
        Lesson lesson = createTestLesson(courseId);

        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file1, file2);

        when(file1.getSize()).thenReturn(1024L);
        when(file2.getSize()).thenReturn(2048L);
        when(file1.getContentType()).thenReturn("image/jpeg");
        when(file2.getContentType()).thenReturn("image/png");
        when(lessonService.getLessonEntity(lessonId)).thenReturn(lesson);
        when(storage.storeMultiple(courseId, lessonId, files)).thenReturn(imageUrls);
        when(bulkImageUploadProcessor.processBulkUpload(
                eq(lessonId), any(), eq(imageUrls), anyList(), anyList())).thenReturn(expectedResponse);

        BulkImageUploadResponseDto result = imageService.uploadBulk(lessonId, requestDto, files);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(fileStorageValidator).validateMultipleFiles(files);
        verify(storage).storeMultiple(courseId, lessonId, files);
    }

    private Lesson createTestLesson(UUID courseId) {
        Course course = new Course();
        course.setId(courseId);
        
        Module module = new Module();
        module.setCourse(course);
        
        Lesson lesson = new Lesson();
        lesson.setModule(module);
        return lesson;
    }
}