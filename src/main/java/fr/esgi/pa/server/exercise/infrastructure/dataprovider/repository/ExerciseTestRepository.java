package fr.esgi.pa.server.exercise.infrastructure.dataprovider.repository;

import fr.esgi.pa.server.exercise.infrastructure.dataprovider.entity.JpaExerciseTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseTestRepository extends JpaRepository<JpaExerciseTest, Long> {
}
