package uz.consortgroup.course_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.consortgroup.course_service.entity.Lesson;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    @Query("SELECT l FROM Lesson l WHERE l.module.id = :moduleId")
    List<Lesson> findAllByModuleId(UUID moduleId);
}
