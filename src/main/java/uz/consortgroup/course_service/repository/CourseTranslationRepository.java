package uz.consortgroup.course_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.course_service.entity.CourseTranslation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CourseTranslationRepository extends JpaRepository<CourseTranslation, UUID> {
    List<CourseTranslation> findByCourseId(UUID courseId);
    Optional<CourseTranslation> findFirstByCourseIdOrderByLanguageAsc(UUID courseId);
    @Query("select ct.slug from CourseTranslation ct where ct.slug in :slugs")
    Set<String> findExistingSlugs(@Param("slugs") Collection<String> slugs);
}
