package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository;

import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ExerciseCaseRepository extends JpaRepository<JpaExerciseCase, Long> {
    Set<JpaExerciseCase> findAllByExerciseId(Long exerciseId);

    Set<JpaExerciseCase> findAllByIdIn(Set<Long> longs);
}
