package uz.consortgroup.course_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.consortgroup.course_service.entity.Resource;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {
    @Query("SELECT r FROM Resource r WHERE r.lesson.id = :id")
    List<Resource> findResourceById(UUID id);
}
