package uz.consortgroup.course_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import uz.consortgroup.core.api.v1.dto.course.request.resource.ResourceCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.course.response.resource.ResourceResponseDto;
import uz.consortgroup.course_service.entity.Resource;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ResourceTranslationMapper.class})
public interface ResourceMapper {
    @Mappings({
            @Mapping(target = "id",       source = "id"),
            @Mapping(target = "lessonId", source = "lesson.id"),
            @Mapping(target = "fileUrl",  source = "fileUrl"),
            @Mapping(target = "fileSize", source = "fileSize"),
            @Mapping(target = "mimeType", source = "mimeType"),
            @Mapping(target = "orderPosition", source = "orderPosition"),
            @Mapping(target = "createdAt", source = "createdAt"),
            @Mapping(target = "updatedAt", source = "updatedAt"),
            @Mapping(target = "translations", source = "translations")
    })
    ResourceResponseDto toResponseDto(Resource resource);
    Resource toEntity(ResourceCreateRequestDto dto);
    List<ResourceResponseDto> toResponseList(List<Resource> resources);
}