package fr.esgi.pa.server.exercise.core.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.entity.Exercise;
import fr.esgi.pa.server.exercise.core.exception.IncorrectExerciseException;

import java.util.Set;

public interface ExerciseDao {
    Exercise createExercise(String title, String description, Long userId) throws NotFoundException;

    Exercise findById(Long exerciseId) throws NotFoundException;

    Set<Exercise> findAll();

    Boolean existsById(Long exerciseId);

    Exercise save(Exercise exercise) throws IncorrectExerciseException;

    void deleteById(Long exerciseId);
}
