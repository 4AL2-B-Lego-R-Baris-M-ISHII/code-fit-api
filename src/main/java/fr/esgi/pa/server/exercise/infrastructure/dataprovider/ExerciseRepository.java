package fr.esgi.pa.server.exercise.infrastructure.dataprovider;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<JpaExercise, Long> {
}
