package uz.consortgroup.course_service.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.course_service.entity.Module;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {
    @EntityGraph(attributePaths = {"translations"})
    List<Module> findByCourseId(UUID courseId);
}
