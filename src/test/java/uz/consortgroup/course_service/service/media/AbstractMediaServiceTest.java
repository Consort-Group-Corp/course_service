package uz.consortgroup.course_service.service.media;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.core.api.v1.dto.course.enumeration.FileType;
import uz.consortgroup.core.api.v1.dto.course.enumeration.MimeType;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.media.processor.AbstractMediaUploadProcessor;
import uz.consortgroup.course_service.service.storage.FileStorageService;
import uz.consortgroup.course_service.validator.FileStorageValidator;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbstractMediaServiceTest {

    @Mock
    private FileStorageService storage;

    @Mock
    private LessonService lessonService;

    @Mock
    private AbstractMediaUploadProcessor<Object, Object, Object, Object> singleProcessor;

    @Mock
    private AbstractMediaUploadProcessor<Object, Object, Object, Object> bulkProcessor;

    @Mock
    private FileStorageValidator fileStorageValidator;

    @Mock
    private MultipartFile file;

    @Mock
    private List<MultipartFile> files;

    @InjectMocks
    private TestMediaUploadService mediaUploadService;

    @BeforeEach
    void setUp() {
        mediaUploadService = new TestMediaUploadService(
                storage,
                lessonService,
                singleProcessor,
                bulkProcessor,
                fileStorageValidator
        );
    }

    private static class TestMediaUploadService extends AbstractMediaUploadService<
            Object, Object, Object, Object,
            AbstractMediaUploadProcessor<Object, Object, Object, Object>,
            AbstractMediaUploadProcessor<Object, Object, Object, Object>> {

        public TestMediaUploadService(
                FileStorageService storage,
                LessonService lessonService,
                AbstractMediaUploadProcessor<Object, Object, Object, Object> singleProcessor,
                AbstractMediaUploadProcessor<Object, Object, Object, Object> bulkProcessor,
                FileStorageValidator fileStorageValidator
        ) {
            super(storage, lessonService, singleProcessor, bulkProcessor, fileStorageValidator);
        }
    }

    @Test
    void upload_WithValidData_ShouldProcessSuccessfully() {
        UUID lessonId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Object requestDto = new Object();
        String fileUrl = "http://example.com/file";
        Lesson lesson = createTestLesson(courseId);
        Object expectedResponse = new Object();

        when(file.getSize()).thenReturn(1000L);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(fileStorageValidator.determineFileType(file)).thenReturn(FileType.IMAGE);
        when(lessonService.getLessonEntity(lessonId)).thenReturn(lesson);
        when(storage.store(courseId, lessonId, file)).thenReturn(fileUrl);
        when(singleProcessor.processSingle(
                eq(lessonId), any(), eq(fileUrl), eq(MimeType.IMAGE_JPEG), eq(1000L)
        )).thenReturn(expectedResponse);

        Object result = mediaUploadService.upload(lessonId, requestDto, file);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(fileStorageValidator).validateFile(file, FileType.IMAGE);
        verify(storage).store(courseId, lessonId, file);
        verify(singleProcessor).processSingle(
                eq(lessonId), any(), eq(fileUrl), eq(MimeType.IMAGE_JPEG), eq(1000L));
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