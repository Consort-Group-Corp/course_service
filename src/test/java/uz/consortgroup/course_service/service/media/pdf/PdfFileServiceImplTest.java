package uz.consortgroup.course_service.service.media.pdf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.course_service.dto.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.course_service.dto.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.course_service.dto.response.pdf.BulkPdfFilesUploadResponseDto;
import uz.consortgroup.course_service.dto.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Module;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.entity.enumeration.FileType;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.media.processor.pdf.BulkPdfFilesUploadProcessor;
import uz.consortgroup.course_service.service.media.processor.pdf.PdfFilesUploadProcessor;
import uz.consortgroup.course_service.service.storage.FileStorageService;
import uz.consortgroup.course_service.validator.FileStorageValidator;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PdfFileServiceImplTest {

    @Mock
    private FileStorageService storage;

    @Mock
    private LessonService lessonService;

    @Mock
    private PdfFilesUploadProcessor pdfUploadProcessor;

    @Mock
    private BulkPdfFilesUploadProcessor bulkPdfUploadProcessor;

    @Mock
    private FileStorageValidator fileStorageValidator;

    @Mock
    private MultipartFile pdfFile;

    @Mock
    private List<MultipartFile> pdfFiles;

    @InjectMocks
    private PdfFileServiceImpl pdfFileService;

    @BeforeEach
    void setUp() {
        pdfFileService = new PdfFileServiceImpl(
            storage,
            lessonService,
            pdfUploadProcessor,
            bulkPdfUploadProcessor,
            fileStorageValidator
        );
    }

    @Test
    void upload_WithValidPdf_ShouldReturnResponse() {
        UUID lessonId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        PdfFileUploadRequestDto requestDto = new PdfFileUploadRequestDto();
        PdfFileUploadResponseDto expectedResponse = new PdfFileUploadResponseDto();
        String fileUrl = "http://example.com/document.pdf";
        Lesson lesson = createTestLesson(courseId);

        when(pdfFile.getSize()).thenReturn(2048L);
        when(pdfFile.getContentType()).thenReturn("application/pdf");
        when(fileStorageValidator.determineFileType(pdfFile)).thenReturn(FileType.PDF);
        when(lessonService.getLessonEntity(lessonId)).thenReturn(lesson);
        when(storage.store(courseId, lessonId, pdfFile)).thenReturn(fileUrl);
        when(pdfUploadProcessor.processSingle(
            eq(lessonId), any(), eq(fileUrl), any(), eq(2048L))
        ).thenReturn(expectedResponse);

        PdfFileUploadResponseDto result = pdfFileService.upload(lessonId, requestDto, pdfFile);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(fileStorageValidator).validateFile(pdfFile, FileType.PDF);
        verify(storage).store(courseId, lessonId, pdfFile);
    }

    @Test
    void uploadBulk_WithValidPdfs_ShouldReturnResponse() {
        UUID lessonId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        BulkPdfFilesUploadRequestDto requestDto = new BulkPdfFilesUploadRequestDto();
        BulkPdfFilesUploadResponseDto expectedResponse = new BulkPdfFilesUploadResponseDto();
        List<String> fileUrls = List.of("doc1.pdf", "doc2.pdf");
        Lesson lesson = createTestLesson(courseId);

        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file1, file2);

        when(file1.getSize()).thenReturn(1024L);
        when(file2.getSize()).thenReturn(2048L);
        when(file1.getContentType()).thenReturn("application/pdf");
        when(file2.getContentType()).thenReturn("application/pdf");
        when(lessonService.getLessonEntity(lessonId)).thenReturn(lesson);
        when(storage.storeMultiple(courseId, lessonId, files)).thenReturn(fileUrls);
        when(bulkPdfUploadProcessor.processBulkUpload(
            eq(lessonId), any(), eq(fileUrls), anyList(), anyList())
        ).thenReturn(expectedResponse);

        BulkPdfFilesUploadResponseDto result = pdfFileService.uploadBulk(lessonId, requestDto, files);

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