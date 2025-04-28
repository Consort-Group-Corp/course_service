package uz.consortgroup.course_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.course_service.entity.ModuleTranslation;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModuleTranslationRepository extends JpaRepository<ModuleTranslation, UUID> {
    List<ModuleTranslation> findByModuleId(UUID moduleId);
}
