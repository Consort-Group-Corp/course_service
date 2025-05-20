package uz.consortgroup.course_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.course_service.entity.Resource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {
    @Query("SELECT r FROM Resource r WHERE r.id = :resourceId AND r.lesson.id = :lessonId")
    Optional<Resource> findByIdAndLesson(@Param("resourceId") UUID resourceId,
                                         @Param("lessonId") UUID lessonId);

    @Query("SELECT r FROM Resource r WHERE r.id IN :ids AND r.lesson.id = :lessonId")
    List<Resource> findAllByIdsAndLesson(@Param("ids")      List<UUID> ids,
                                         @Param("lessonId") UUID lessonId);
}
