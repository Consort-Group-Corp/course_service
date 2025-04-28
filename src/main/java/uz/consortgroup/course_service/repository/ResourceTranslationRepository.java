package uz.consortgroup.course_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.consortgroup.course_service.entity.ResourceTranslation;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResourceTranslationRepository extends JpaRepository<ResourceTranslation, UUID> {
    @Query("SELECT rt FROM ResourceTranslation rt WHERE rt.resource.id = :id")
    List<ResourceTranslation> findResourceTranslationById(UUID id);
}
