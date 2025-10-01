package uz.consortgroup.course_service.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.core.api.v1.dto.course.enumeration.CourseStatus;
import uz.consortgroup.course_service.entity.Course;
import uz.consortgroup.course_service.entity.Lesson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.translations WHERE c.id = :id")
    Optional<Course> findByIdWithTranslations(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"translations"})
    @Query("SELECT c FROM Course c WHERE c.id = :id AND c.courseStatus = :status")
    Optional<Course> findCourseWithTranslations(@Param("id") UUID id, @Param("status") CourseStatus status);

    @Query("select c.authorId from Course c where c.id = :courseId")
    Optional<UUID> findMentorIdById(@Param("courseId") UUID courseId);
}
