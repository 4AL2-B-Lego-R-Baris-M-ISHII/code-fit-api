package fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository;

import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExercise;
import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExerciseCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseCaseRepository extends JpaRepository<JpaExerciseCase, Long> {
    Optional<JpaExerciseCase> findByExercise(JpaExercise exercise);
}
