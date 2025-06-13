package uz.consortgroup.course_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.course_service.entity.CourseTranslation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseTranslationRepository extends JpaRepository<CourseTranslation, UUID> {
    List<CourseTranslation> findByCourseId(UUID courseId);
    Optional<CourseTranslation> findFirstByCourseIdOrderByLanguageAsc(UUID courseId);
}
