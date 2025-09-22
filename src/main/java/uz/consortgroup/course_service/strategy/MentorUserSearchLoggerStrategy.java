package uz.consortgroup.course_service.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.core.api.v1.dto.user.enumeration.UserRole;
import uz.consortgroup.course_service.service.event.mentor.MentorActionLogger;


import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorUserSearchLoggerStrategy  {
    private final MentorActionLogger mentorLogger;

    public void log(UUID actorId, UUID mentorId) {
        log.info("Mentor (actorId={}) searching user (targetUserId={})", actorId, mentorId);
        mentorLogger.logUserSearch(actorId, mentorId);
    }

    public UserRole getSupportedRole() {
        return UserRole.MENTOR;
    }
}
