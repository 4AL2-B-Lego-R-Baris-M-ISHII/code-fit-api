package fr.esgi.pa.server.code.infrastructure.dataprovider.repository;

import fr.esgi.pa.server.code.infrastructure.dataprovider.entity.JpaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<JpaCode, Long> {
    Optional<JpaCode> findByUserIdAndExerciseCaseId(Long userId, Long exerciseCaseId);
}
