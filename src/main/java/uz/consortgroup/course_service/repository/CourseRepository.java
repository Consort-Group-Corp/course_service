package uz.consortgroup.course_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.course_service.entity.Course;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.translations WHERE c.id = :id")
    Optional<Course> findByIdWithTranslations(@Param("id") UUID id);
}
