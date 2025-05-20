package uz.consortgroup.course_service.service.media.pdf;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.course.request.pdf.BulkPdfFilesUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.request.pdf.PdfFileUploadRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.pdf.BulkPdfFilesUploadResponseDto;
import uz.consortgroup.core.api.v1.dto.course.response.pdf.PdfFileUploadResponseDto;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.repository.ResourceRepository;
import uz.consortgroup.course_service.service.lesson.LessonService;
import uz.consortgroup.course_service.service.media.AbstractMediaUploadService;
import uz.consortgroup.course_service.service.media.processor.pdf.BulkPdfFilesUploadProcessor;
import uz.consortgroup.course_service.service.media.processor.pdf.PdfFilesUploadProcessor;
import uz.consortgroup.course_service.service.storage.FileStorageService;
import uz.consortgroup.course_service.validator.FileStorageValidator;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PdfFileServiceImpl extends AbstractMediaUploadService<PdfFileUploadRequestDto, PdfFileUploadResponseDto, BulkPdfFilesUploadRequestDto,
        BulkPdfFilesUploadResponseDto, PdfFilesUploadProcessor, BulkPdfFilesUploadProcessor> implements PdfFileService {

    private final ResourceRepository resourceRepository;

    public PdfFileServiceImpl(FileStorageService storage, LessonService lessonService, PdfFilesUploadProcessor pdfUploadProcessor,
                              BulkPdfFilesUploadProcessor bulkPdfUploadProcessor, FileStorageValidator fileStorageValidator,
                              ResourceRepository resourceRepository) {

        super(storage, lessonService, pdfUploadProcessor, bulkPdfUploadProcessor, fileStorageValidator);
        this.resourceRepository = resourceRepository;
    }

    @Override
    @Transactional
    @AllAspect
    public void delete(UUID lessonId, UUID resourceId) {
        Resource res = resourceRepository.findByIdAndLesson(resourceId, lessonId).orElseThrow(() -> new EntityNotFoundException("Resource not found"));
        storage.delete(res.getFileUrl());
        resourceRepository.delete(res);
    }

    @Override
    @Transactional
    @AllAspect
    public void deleteBulk(UUID lessonId, List<UUID> resourceIds) {
        List<Resource> resources = resourceRepository.findAllByIdsAndLesson(resourceIds, lessonId);
        for (Resource res : resources) {
            log.info(" - Deleting resource id={} url={}", res.getId(), res.getFileUrl());
            storage.delete(res.getFileUrl());
        }
        resourceRepository.deleteAll(resources);
    }
}