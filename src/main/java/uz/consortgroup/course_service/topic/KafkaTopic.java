package uz.consortgroup.course_service.topic;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KafkaTopic {
    @Value("${kafka.mentor-action}")
    private String mentorActionTopic;
}
