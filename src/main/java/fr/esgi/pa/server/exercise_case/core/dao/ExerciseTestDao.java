package fr.esgi.pa.server.exercise_case.core.dao;

import fr.esgi.pa.server.common.core.exception.NotFoundException;
import fr.esgi.pa.server.exercise_case.core.entity.ExerciseTest;

import java.util.Set;

public interface ExerciseTestDao {
    Set<ExerciseTest> findAllByExerciseCaseId(Long exerciseCaseId) throws NotFoundException;

    void deleteAllByExerciseCaseId(Long exerciseCaseId);
}
