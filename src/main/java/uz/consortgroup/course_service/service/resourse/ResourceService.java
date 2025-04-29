package uz.consortgroup.course_service.service.resourse;

import uz.consortgroup.course_service.dto.request.module.ModuleCreateRequestDto;
import uz.consortgroup.course_service.entity.Lesson;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.enumeration.MimeType;
import uz.consortgroup.course_service.entity.enumeration.ResourceType;

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
