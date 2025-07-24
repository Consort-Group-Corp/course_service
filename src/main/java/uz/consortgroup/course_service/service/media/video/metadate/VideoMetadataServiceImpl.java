package uz.consortgroup.course_service.service.media.video.metadate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.course_service.entity.Resource;
import uz.consortgroup.course_service.entity.VideoMetaData;
import uz.consortgroup.course_service.repository.VideoMetaDataRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class VideoMetadataServiceImpl implements VideoMetadataService {
    private final VideoMetaDataRepository videoMetaDataRepository;

    @Override
    @Transactional
    public VideoMetaData create(UUID resourceId, Integer durationSeconds, String resolution) {
        log.debug("Creating video metadata for resourceId={}, duration={}, resolution={}",
                resourceId, durationSeconds, resolution);

        VideoMetaData meta = VideoMetaData.builder()
                .resource(Resource.builder().id(resourceId).build())
                .duration(durationSeconds)
                .resolution(resolution)
                .build();

        return videoMetaDataRepository.save(meta);
    }

    @Override
    public List<VideoMetaData> saveAll(List<VideoMetaData> metaDataList) {
        log.debug("Saving list of video metadata entries. Count={}", metaDataList.size());
        return videoMetaDataRepository.saveAll(metaDataList);
    }
}
