package uz.consortgroup.course_service.service.resourse;

import uz.consortgroup.core.api.v1.dto.course.enumeration.MimeType;
import uz.consortgroup.core.api.v1.dto.course.enumeration.ResourceType;
import uz.consortgroup.core.api.v1.dto.course.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Resource;

import java.util.List;
import java.util.UUID;

public interface ResourceService {
    Resource create(UUID lessonId,
                    ResourceType resourceType,
                    String fileUrl,
                    Long fileSize,
                    MimeType mimeType,
                    Integer orderPosition);

    List<Resource> createBulk(List<ModuleCreateRequestDto> moduleDtos, List<Lesson> lessons);
    List<Resource> saveAllResources(List<Resource> resources);
}
