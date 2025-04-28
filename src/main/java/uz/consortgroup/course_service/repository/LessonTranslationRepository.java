package uz.consortgroup.course_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.consortgroup.course_service.entity.LessonTranslation;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonTranslationRepository extends JpaRepository<LessonTranslation, UUID> {
    @Query("SELECT lt FROM LessonTranslation lt WHERE lt.lesson.id = :id")
    List<LessonTranslation> findLessonTranslationById(UUID id);
}
