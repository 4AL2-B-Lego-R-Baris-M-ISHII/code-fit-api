package fr.esgi.pa.server.exercise.core.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise.core.entity.ExerciseTest;

import java.util.Set;

public interface ExerciseTestDao {
    Set<ExerciseTest> findAllByExerciseCaseId(Long exerciseCaseId) throws NotFoundException;
}
