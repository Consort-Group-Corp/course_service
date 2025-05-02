package uz.consortgroup.course_service.service.media.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.enumeration.FileType;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.storage.FileStorageService;
import uz.consortgroup.course_service.validator.FileStorageValidator;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class AbstractMediaUploadService<SingleRequestDto, SingleResponseDto, BulkRequestDto, BulkResponseDto, SingleProcessor
        extends AbstractMediaUploadProcessor<SingleRequestDto, SingleResponseDto, BulkRequestDto, BulkResponseDto>, BulkProcessor
        extends AbstractMediaUploadProcessor<SingleRequestDto, SingleResponseDto, BulkRequestDto, BulkResponseDto>> {

    protected final FileStorageService storage;
    protected final LessonService lessonService;
    protected final SingleProcessor singleProcessor;
    protected final BulkProcessor bulkProcessor;
    protected final FileStorageValidator fileStorageValidator;

    @Transactional
    @AllAspect
    public SingleResponseDto upload(UUID lessonId, SingleRequestDto dto, MultipartFile file) {
        FileType fileType = fileStorageValidator.determineFileType(file);
        fileStorageValidator.validateFile(file, fileType);
        Lesson lesson = getLesson(lessonId);
        UUID courseId = lesson.getModule().getCourse().getId();

        long fileSize = file.getSize();
        MimeType mimeType = MimeType.fromContentType(file.getContentType());
        String fileUrl = storage.store(courseId, lessonId, file);

        return singleProcessor.processSingle(lessonId, dto, fileUrl, mimeType, fileSize);
    }

    @Transactional
    @AllAspect
    public BulkResponseDto uploadBulk(UUID lessonId, BulkRequestDto dto, List<MultipartFile> files) {
        fileStorageValidator.validateMultipleFiles(files);
        fileStorageValidator.validateBulk(dto, files);
        Lesson lesson = getLesson(lessonId);
        UUID courseId = lesson.getModule().getCourse().getId();

        List<String> fileUrls = storage.storeMultiple(courseId, lessonId, files);
        List<Long> fileSizes = files.stream().map(MultipartFile::getSize).toList();
        List<MimeType> mimeTypes = files.stream()
                .map(file -> MimeType.fromContentType(file.getContentType()))
                .toList();

        return bulkProcessor.processBulkUpload(lessonId, dto, fileUrls, mimeTypes, fileSizes);
    }

    private Lesson getLesson(UUID lessonId) {
        return lessonService.getLessonEntity(lessonId);
    }
}