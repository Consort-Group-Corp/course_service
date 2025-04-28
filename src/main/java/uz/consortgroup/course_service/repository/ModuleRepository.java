package uz.consortgroup.course_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.course_service.entity.Module;

import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {
}
