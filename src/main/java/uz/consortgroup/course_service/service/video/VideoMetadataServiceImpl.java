package uz.consortgroup.course_service.service.video;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.course_service.asspect.annotation.AllAspect;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.VideoMetaData;
import uz.consortgroup.course_service.repository.VideoMetaDataRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class VideoMetadataServiceImpl implements VideoMetadataService {
    private final VideoMetaDataRepository videoMetaDataRepository;

    @Override
    @Transactional
    @AllAspect
    public VideoMetaData create(UUID resourceId, Integer durationSeconds, String resolution) {
        VideoMetaData meta = VideoMetaData.builder()
                .resource(Resource.builder().id(resourceId).build())
                .duration(durationSeconds)
                .resolution(resolution)
                .build();
        return videoMetaDataRepository.save(meta);
    }

    @Override
    @AllAspect
    public List<VideoMetaData> saveAll(List<VideoMetaData> metaDataList) {
        return videoMetaDataRepository.saveAll(metaDataList);
    }
}
