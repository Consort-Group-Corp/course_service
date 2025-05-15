package uz.consortgroup.course_service.service.media.processor;

import lombok.RequiredArgsConstructor;
import uz.consortgroup.core.api.v1.dto.course.enumeration.MimeType;
import uz.consortgroup.core.api.v1.dto.course.response.resource.ResourceTranslationResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.mapper.ResourceTranslationMapper;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.translation.ResourceTranslationService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractMediaUploadProcessor<
        SingleRequestDto, SingleResponseDto, BulkRequestDto, BulkResponseDto>
        implements MediaUploadProcessor<SingleRequestDto, SingleResponseDto>,
        BulkMediaUploadProcessor<SingleRequestDto, BulkRequestDto, BulkResponseDto> {

    protected final ResourceService resourceService;
    protected final ResourceTranslationService translationService;
    protected final ResourceTranslationMapper translationMapper;

    @Override
    public SingleResponseDto processSingle(UUID lessonId, SingleRequestDto dto, String fileUrl, MimeType mimeType, long fileSize) {
        Resource res = createResource(lessonId, dto, fileUrl, mimeType, fileSize);
        saveTranslations(dto, res);
        return buildSingleResponse(res, dto);
    }

    @Override
    public BulkResponseDto processBulkUpload(UUID lessonId, BulkRequestDto bulkDto, List<String> fileUrls, List<MimeType> mimeTypes, List<Long> fileSizes) {
        List<SingleRequestDto> dtos = extractDtos(bulkDto);
        List<Resource> resources = prepareResources(lessonId, dtos, fileUrls, mimeTypes, fileSizes);
        resources = resourceService.saveAllResources(resources);
        saveAllTranslations(dtos, resources);
        return buildBulkResponse(resources, dtos);
    }

    protected abstract List<SingleRequestDto> extractDtos(BulkRequestDto bulkDto);

    protected abstract Resource createResource(UUID lessonId, SingleRequestDto dto, String fileUrl, MimeType mimeType, long fileSize);

    protected abstract List<Resource> prepareResources(UUID lessonId, List<SingleRequestDto> dtos, List<String> fileUrls, List<MimeType> mimeTypes, List<Long> fileSizes);

    protected abstract void saveTranslations(SingleRequestDto dto, Resource resource);

    protected abstract void saveAllTranslations(List<SingleRequestDto> dtos, List<Resource> resources);

    protected abstract SingleResponseDto buildSingleResponse(Resource resource, SingleRequestDto dto);

    protected abstract BulkResponseDto buildBulkResponse(List<Resource> resources, List<SingleRequestDto> dtos);

    protected List<ResourceTranslationResponseDto> mapTranslations(List<ResourceTranslation> translations) {
        if (translations == null || translations.isEmpty()) return List.of();
        return translations.stream()
                .map(translationMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}