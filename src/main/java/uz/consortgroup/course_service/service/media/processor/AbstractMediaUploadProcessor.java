package uz.consortgroup.course_service.service.media.processor;

import lombok.RequiredArgsConstructor;
import uz.consortgroup.course_service.dto.response.resource.ResourceTranslationResponseDto;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.ResourceTranslation;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.mapper.ResourceTranslationMapper;
import uz.consortgroup.course_service.service.resourse.ResourceService;
import uz.consortgroup.course_service.service.resourse.ResourceTranslationService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractMediaUploadProcessor<ReqDto, SingleRes, BulkRes> {
    protected final ResourceService resourceService;
    protected final ResourceTranslationService translationService;
    protected final ResourceTranslationMapper translationMapper;

    public SingleRes processSingle(UUID lessonId, ReqDto dto, String fileUrl, MimeType mimeType) {
        Resource res = createResource(lessonId, dto, fileUrl, mimeType);
        saveTranslations(dto, res);
        return buildSingleResponse(res, dto); // Передаем dto
    }

    public BulkRes processBulk(UUID lessonId, List<ReqDto> dtos, List<String> fileUrls) {
        List<Resource> resources = prepareResources(lessonId, dtos, fileUrls);
        resources = resourceService.saveAllResources(resources);
        saveAllTranslations(dtos, resources);
        return buildBulkResponse(resources, dtos); // Передаем dtos
    }

    protected abstract Resource createResource(UUID lessonId, ReqDto dto, String fileUrl, MimeType mimeType);
    protected abstract List<Resource> prepareResources(UUID lessonId, List<ReqDto> dtos, List<String> fileUrls);
    protected abstract void saveTranslations(ReqDto dto, Resource resource);
    protected abstract void saveAllTranslations(List<ReqDto> dtos, List<Resource> resources);
    protected abstract SingleRes buildSingleResponse(Resource resource, ReqDto dto); // Обновленная сигнатура
    protected abstract BulkRes buildBulkResponse(List<Resource> resources, List<ReqDto> dtos); // Обновленная сигнатура

    protected List<ResourceTranslationResponseDto> mapTranslations(List<ResourceTranslation> translations) {
        if (translations == null || translations.isEmpty()) return List.of();
        return translations.stream()
                .map(translationMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}