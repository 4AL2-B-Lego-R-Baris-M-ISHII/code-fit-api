package fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.repository;

import fr.esgi.pa.server.exercise_case.infrastructure.dataprovider.entity.JpaExerciseTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ExerciseTestRepository extends JpaRepository<JpaExerciseTest, Long> {
    Set<JpaExerciseTest> findAllByExerciseCaseId(Long exerciseCaseId);
}
