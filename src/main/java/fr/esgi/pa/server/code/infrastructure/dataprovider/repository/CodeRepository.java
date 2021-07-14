package fr.esgi.pa.server.code.infrastructure.dataprovider.repository;

import fr.esgi.pa.server.code.infrastructure.dataprovider.entity.JpaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CodeRepository extends JpaRepository<JpaCode, Long> {
    Optional<JpaCode> findByUserIdAndExerciseCaseId(Long userId, Long exerciseCaseId);

    Set<JpaCode> findAllByUserId(Long userId);
}
