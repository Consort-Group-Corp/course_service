package uz.consortgroup.course_service.service.media.video;

import uz.consortgroup.course_service.entity.VideoMetaData;

import java.util.List;
import java.util.UUID;

public interface VideoMetadataService {
    VideoMetaData create(UUID resourceId, Integer durationSeconds, String resolution);
    List<VideoMetaData> saveAll(List<VideoMetaData> metaDataList);
}
